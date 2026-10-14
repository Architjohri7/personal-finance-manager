package com.syfe.pfm;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.math.*;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api")
class ApiController {
 private final UserRepository users; private final CategoryRepository categories; private final TransactionRepository transactions; private final GoalRepository goals; private final PasswordEncoder encoder;
 ApiController(UserRepository u,CategoryRepository c,TransactionRepository t,GoalRepository g,PasswordEncoder e){users=u;categories=c;transactions=t;goals=g;encoder=e;}
 @GetMapping("/health") Map<String,String> health(){return Map.of("message","Personal Finance Manager API is running!");}
 @PostMapping("/auth/register") @Transactional ResponseEntity<?> register(@Valid @RequestBody RegisterRequest r){
  if(users.existsByUsernameIgnoreCase(r.username())) throw new ResponseStatusException(HttpStatus.CONFLICT,"Username already registered");
  AppUser u=users.save(new AppUser(r.username().toLowerCase(Locale.ROOT),encoder.encode(r.password()),r.fullName().trim(),r.phoneNumber().trim()));
  categories.save(new Category(u,"Salary",TransactionType.INCOME,false));
  for(String n:List.of("Food","Rent","Transportation","Entertainment","Healthcare","Utilities")) categories.save(new Category(u,n,TransactionType.EXPENSE,false));
  return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message","User registered successfully","userId",u.id));
 }
 @PostMapping("/auth/login") ResponseEntity<?> login(@Valid @RequestBody LoginRequest r,HttpServletRequest request){
  AppUser u=users.findByUsernameIgnoreCase(r.username()).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid username or password"));
  if(!encoder.matches(r.password(),u.password)) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid username or password");
  request.getSession(true);
  request.changeSessionId();
  Authentication auth=new UsernamePasswordAuthenticationToken(u.username,null,AuthorityUtils.createAuthorityList("ROLE_USER"));
  SecurityContextHolder.getContext().setAuthentication(auth); request.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT",SecurityContextHolder.getContext());
  return ResponseEntity.ok(new MessageResponse("Login successful"));
 }
 @PostMapping("/auth/logout") ResponseEntity<?> logout(HttpServletRequest req){HttpSession s=req.getSession(false);if(s!=null)s.invalidate();SecurityContextHolder.clearContext();return ResponseEntity.ok(new MessageResponse("Logout successful"));}
 private AppUser current(){String username=SecurityContextHolder.getContext().getAuthentication().getName();return users.findByUsernameIgnoreCase(username).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Session user not found"));}
 private Category category(String name,AppUser u){return categories.findByOwnerIdAndNameIgnoreCaseAndType(u.id,name, inferType(name,u)).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"Unknown category for this user: "+name));}
 private TransactionType inferType(String name,AppUser u){return categories.findByOwnerIdOrderByTypeAscNameAsc(u.id).stream().filter(c->c.name.equalsIgnoreCase(name)).map(c->c.type).findFirst().orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"Unknown category: "+name));}
 private TransactionResponse tx(FinanceTransaction t){return new TransactionResponse(t.id,t.amount,t.date,t.category.name,t.description,t.category.type);}
 @PostMapping("/transactions") ResponseEntity<?> createTransaction(@Valid @RequestBody TransactionRequest r){AppUser u=current();Category c=category(r.category(),u);return ResponseEntity.status(HttpStatus.CREATED).body(tx(transactions.save(new FinanceTransaction(u,c,r.amount(),r.date(),r.description()))));}
 @GetMapping("/transactions") Map<String,Object> listTransactions(@RequestParam(required=false) LocalDate startDate,@RequestParam(required=false) LocalDate endDate,@RequestParam(required=false) Long categoryId,@RequestParam(required=false) TransactionType type){
  AppUser u=current();LocalDate start=startDate==null?LocalDate.of(1900,1,1):startDate;LocalDate end=endDate==null?LocalDate.now():endDate;if(start.isAfter(end))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"startDate must be on or before endDate");
  List<FinanceTransaction> list=transactions.findByOwnerIdAndDateBetweenOrderByDateDesc(u.id,start,end);
  if(categoryId!=null) {categories.findByIdAndOwnerId(categoryId,u.id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Category not found"));list=list.stream().filter(t->t.category.id.equals(categoryId)).toList();}
  if(type!=null) list=list.stream().filter(t->t.category.type==type).toList();
  return Map.of("transactions",list.stream().map(this::tx).toList());
 }
 @PutMapping("/transactions/{id}") @Transactional Object updateTransaction(@PathVariable Long id,@RequestBody Map<String,Object> body){AppUser u=current();FinanceTransaction t=transactions.findByIdAndOwnerId(id,u.id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Transaction not found"));
  if(body.containsKey("date"))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Transaction date cannot be changed");
  if(body.containsKey("amount")){try{BigDecimal a=new BigDecimal(body.get("amount").toString());if(a.compareTo(BigDecimal.ZERO)<=0)throw new Exception();t.amount=a;}catch(Exception ex){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Amount must be positive");}}
  if(body.containsKey("description")){Object d=body.get("description");String s=d==null?null:d.toString();if(s!=null&&s.length()>500)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Description is too long");t.description=s;}
  if(body.containsKey("category")){Category c=category(body.get("category").toString(),u);t.category=c;}return tx(transactions.save(t));
 }
 @DeleteMapping("/transactions/{id}") Object deleteTransaction(@PathVariable Long id){AppUser u=current();FinanceTransaction t=transactions.findByIdAndOwnerId(id,u.id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Transaction not found"));transactions.delete(t);return new MessageResponse("Transaction deleted successfully");}
 @GetMapping("/categories") Map<String,Object> listCategories(){return Map.of("categories",categories.findByOwnerIdOrderByTypeAscNameAsc(current().id).stream().map(c->new CategoryResponse(c.id,c.name,c.type,c.isCustom)).toList());}
 @PostMapping("/categories") ResponseEntity<?> createCategory(@Valid @RequestBody CategoryRequest r){AppUser u=current();if(categories.existsByOwnerIdAndNameIgnoreCaseAndType(u.id,r.name().trim(),r.type()))throw new ResponseStatusException(HttpStatus.CONFLICT,"Category already exists");Category c=categories.save(new Category(u,r.name().trim(),r.type(),true));return ResponseEntity.status(HttpStatus.CREATED).body(new CategoryResponse(c.id,c.name,c.type,c.isCustom));}
 @DeleteMapping("/categories/{id}") Object deleteCategory(@PathVariable Long id){AppUser u=current();Category c=categories.findByIdAndOwnerId(id,u.id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Category not found"));if(!c.isCustom)throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Default categories cannot be deleted");if(transactions.existsByCategoryId(id))throw new ResponseStatusException(HttpStatus.CONFLICT,"Category is referenced by transactions");categories.delete(c);return new MessageResponse("Category deleted successfully");}
 private GoalResponse goal(SavingsGoal g){List<FinanceTransaction> ts=transactions.findByOwnerIdAndDateBetweenOrderByDateDesc(g.owner.id,g.startDate,LocalDate.now());BigDecimal income=ts.stream().filter(t->t.category.type==TransactionType.INCOME).map(t->t.amount).reduce(BigDecimal.ZERO,BigDecimal::add);BigDecimal expense=ts.stream().filter(t->t.category.type==TransactionType.EXPENSE).map(t->t.amount).reduce(BigDecimal.ZERO,BigDecimal::add);BigDecimal progress=income.subtract(expense).max(BigDecimal.ZERO);BigDecimal pct=progress.multiply(new BigDecimal("100")).divide(g.targetAmount,2,RoundingMode.HALF_UP);return new GoalResponse(g.id,g.goalName,g.targetAmount,g.targetDate,g.startDate,progress,pct, g.targetAmount.subtract(progress).max(BigDecimal.ZERO));}
 @PostMapping("/goals") ResponseEntity<?> createGoal(@Valid @RequestBody GoalRequest r){AppUser u=current();SavingsGoal g=goals.save(new SavingsGoal(u,r.goalName().trim(),r.targetAmount(),r.targetDate(),LocalDate.now()));return ResponseEntity.status(HttpStatus.CREATED).body(goal(g));}
 @GetMapping("/goals") Map<String,Object> listGoals(){return Map.of("goals",goals.findByOwnerIdOrderByTargetDateAsc(current().id).stream().map(this::goal).toList());}
 @GetMapping("/goals/{id}") Object getGoal(@PathVariable Long id){AppUser u=current();return goal(goals.findByIdAndOwnerId(id,u.id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Goal not found")));}
 @PutMapping("/goals/{id}") Object updateGoal(@PathVariable Long id,@Valid @RequestBody GoalUpdateRequest r){AppUser u=current();SavingsGoal g=goals.findByIdAndOwnerId(id,u.id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Goal not found"));g.targetAmount=r.targetAmount();g.targetDate=r.targetDate();return goal(goals.save(g));}
 @DeleteMapping("/goals/{id}") Object deleteGoal(@PathVariable Long id){AppUser u=current();SavingsGoal g=goals.findByIdAndOwnerId(id,u.id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Goal not found"));goals.delete(g);return new MessageResponse("Goal deleted successfully");}
 @GetMapping("/reports/monthly/{year}/{month}") Object monthly(@PathVariable int year,@PathVariable int month){if(month<1||month>12)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"month must be 1-12");return report(year,month);}
 @GetMapping("/reports/yearly/{year}") Object yearly(@PathVariable int year){Map<String,Object> out=new LinkedHashMap<>();out.put("year",year);Map<String,BigDecimal> inc=new TreeMap<>(),exp=new TreeMap<>();for(int m=1;m<=12;m++){Map<String,Object> r=report(year,m);merge(inc,(Map<String,BigDecimal>)r.get("totalIncome"));merge(exp,(Map<String,BigDecimal>)r.get("totalExpenses"));}out.put("totalIncome",inc);out.put("totalExpenses",exp);out.put("netSavings",sum(inc).subtract(sum(exp)));return out;}
 private Map<String,Object> report(int year,int month){AppUser u=current();LocalDate start=LocalDate.of(year,month,1),end=start.withDayOfMonth(start.lengthOfMonth());List<FinanceTransaction> ts=transactions.findByOwnerIdAndDateBetweenOrderByDateDesc(u.id,start,end);Map<String,BigDecimal> inc=new TreeMap<>(),exp=new TreeMap<>();for(FinanceTransaction t:ts){Map<String,BigDecimal> target=t.category.type==TransactionType.INCOME?inc:exp;target.merge(t.category.name,t.amount,BigDecimal::add);}Map<String,Object> out=new LinkedHashMap<>();out.put("month",month);out.put("year",year);out.put("totalIncome",inc);out.put("totalExpenses",exp);out.put("netSavings",sum(inc).subtract(sum(exp)));return out;}
 private BigDecimal sum(Map<String,BigDecimal> m){return m.values().stream().reduce(BigDecimal.ZERO,BigDecimal::add);}private void merge(Map<String,BigDecimal> a,Map<String,BigDecimal> b){b.forEach((k,v)->a.merge(k,v,BigDecimal::add));}
}

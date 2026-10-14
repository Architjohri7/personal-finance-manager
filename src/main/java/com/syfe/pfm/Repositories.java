package com.syfe.pfm;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.*;

interface UserRepository extends JpaRepository<AppUser,Long> { Optional<AppUser> findByUsernameIgnoreCase(String username); boolean existsByUsernameIgnoreCase(String username); }
interface CategoryRepository extends JpaRepository<Category,Long> { List<Category> findByOwnerIdOrderByTypeAscNameAsc(Long ownerId); Optional<Category> findByIdAndOwnerId(Long id,Long ownerId); Optional<Category> findByOwnerIdAndNameIgnoreCaseAndType(Long ownerId,String name,TransactionType type); boolean existsByOwnerIdAndNameIgnoreCaseAndType(Long ownerId,String name,TransactionType type); }
interface TransactionRepository extends JpaRepository<FinanceTransaction,Long> {
 List<FinanceTransaction> findByOwnerIdOrderByDateDescIdDesc(Long ownerId);
 Optional<FinanceTransaction> findByIdAndOwnerId(Long id,Long ownerId);
 List<FinanceTransaction> findByOwnerIdAndDateBetweenOrderByDateDesc(Long ownerId,LocalDate start,LocalDate end);
 List<FinanceTransaction> findByOwnerIdAndCategoryIdAndDateBetweenOrderByDateDesc(Long ownerId,Long categoryId,LocalDate start,LocalDate end);
 List<FinanceTransaction> findByOwnerIdAndDateBetweenAndCategoryIdOrderByDateDesc(Long ownerId,LocalDate start,LocalDate end,Long categoryId);
 boolean existsByCategoryId(Long categoryId);
}
interface GoalRepository extends JpaRepository<SavingsGoal,Long> { List<SavingsGoal> findByOwnerIdOrderByTargetDateAsc(Long ownerId); Optional<SavingsGoal> findByIdAndOwnerId(Long id,Long ownerId); }

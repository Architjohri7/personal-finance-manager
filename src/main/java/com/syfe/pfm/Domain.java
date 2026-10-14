package com.syfe.pfm;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity @Table(name="app_users", uniqueConstraints=@UniqueConstraint(columnNames="username"))
class AppUser {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
 @Column(nullable=false, unique=true) String username;
 @Column(nullable=false) String password;
 @Column(nullable=false) String fullName;
 @Column(nullable=false) String phoneNumber;
 protected AppUser() {}
 AppUser(String username,String password,String fullName,String phoneNumber){this.username=username;this.password=password;this.fullName=fullName;this.phoneNumber=phoneNumber;}
}

enum TransactionType { INCOME, EXPENSE }
@Entity @Table(name="categories")
class Category {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
 @ManyToOne(optional=false) AppUser owner;
 @Column(nullable=false) String name;
 @Enumerated(EnumType.STRING) @Column(nullable=false) TransactionType type;
 boolean isCustom;
 protected Category() {}
 Category(AppUser owner,String name,TransactionType type,boolean custom){this.owner=owner;this.name=name;this.type=type;this.isCustom=custom;}
}
@Entity @Table(name="transactions")
class FinanceTransaction {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
 @ManyToOne(optional=false) AppUser owner;
 @ManyToOne(optional=false) Category category;
 @Column(nullable=false, precision=14, scale=2) BigDecimal amount;
 @Column(nullable=false) LocalDate date;
 @Column(length=500) String description;
 protected FinanceTransaction() {}
 FinanceTransaction(AppUser owner,Category category,BigDecimal amount,LocalDate date,String description){this.owner=owner;this.category=category;this.amount=amount;this.date=date;this.description=description;}
}
@Entity @Table(name="savings_goals")
class SavingsGoal {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
 @ManyToOne(optional=false) AppUser owner;
 @Column(nullable=false) String goalName;
 @Column(nullable=false, precision=14, scale=2) BigDecimal targetAmount;
 @Column(nullable=false) LocalDate targetDate;
 @Column(nullable=false) LocalDate startDate;
 protected SavingsGoal() {}
 SavingsGoal(AppUser owner,String name,BigDecimal amount,LocalDate targetDate,LocalDate startDate){this.owner=owner;this.goalName=name;this.targetAmount=amount;this.targetDate=targetDate;this.startDate=startDate;}
}

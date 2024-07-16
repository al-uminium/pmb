drop database if exists ppm;

create database ppm;

use ppm;

create table User (
  user_id CHAR(36) NOT NULL, 
  username VARCHAR(100) NOT NULL, 
  email VARCHAR(255),
  paypalEmail VARCHAR(255),
  hashpw VARCHAR(255),
  registration_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT unique_email UNIQUE (email),
  PRIMARY KEY (user_id)
);

create table Expenditures (
  expenditure_id CHAR(36) NOT NULL, 
  expenditure_name VARCHAR(255) NOT NULL, 
  default_currency VARCHAR(3) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (expenditure_id)
);

CREATE TABLE Expense (
  expense_id CHAR(36) NOT NULL,
  owner_id VARCHAR(36) NOT NULL,
  total_cost DECIMAL(10,2) NOT NULL,
  expenditure_id VARCHAR(36) NOT NULL,
  expense_name VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_repayment BOOLEAN DEFAULT 0,
  FOREIGN KEY (owner_id) REFERENCES User (user_id),
  FOREIGN KEY (expenditure_id) REFERENCES Expenditures (expenditure_id),
  PRIMARY KEY (expense_id)
);

CREATE TABLE Expense_Users (
  expense_id CHAR(36) NOT NULL,
  user_id CHAR(36) NOT NULL,
  PRIMARY KEY (expense_id, user_id),
  FOREIGN KEY (expense_id) REFERENCES Expense (expense_id),
  FOREIGN KEY (user_id) REFERENCES User (user_id)
);

create table Expenditure_User (
  expenditure_id CHAR(36), 
  user_id CHAR(36), 
  linked_user_id CHAR(36),
  balance DECIMAL(10,2) DEFAULT 0.00,
  PRIMARY KEY (expenditure_id, user_id),
  FOREIGN KEY (expenditure_id) REFERENCES Expenditures (expenditure_id),
  FOREIGN KEY (user_id) REFERENCES User (user_id)
);

CREATE TABLE Invites (
  invite_token VARCHAR(255) NOT NULL,
  expenditure_id CHAR(36),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (expenditure_id) REFERENCES Expenditures (expenditure_id),
  PRIMARY KEY (expenditure_id)
);

  -- expire_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP + INTERVAL 60 DAY, 
  -- idk if i need this yet
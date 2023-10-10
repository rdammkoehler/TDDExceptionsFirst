# TDD Exceptions First Experiment

Video Link: 10 Oct 2023 Version: https://youtu.be/MCJCEkor-1M

## Testing Methodology

Normal approach is to use ZOMBIES, see https://blog.wingman-sw.com/tdd-guided-by-zombies

Z – Zero
O – One
M – Many (or More complex)
B – Boundary Behaviors
I – Interface definition
E – Exercise Exceptional behavior
S – Simple Scenarios, Simple Solutions

## Alternative Approach

So what if we try to test all the exception paths first?

E – Exercise Exceptional behavior
Z – Zero
O – One
M – Many (or More complex)
B – Boundary Behaviors
I – Interface definition
S – Simple Scenarios, Simple Solutions

## Sample Problem, Bank Accounts with Side Effects

Given you have a bank account
When you make a deposit
Then the balance increases by the amount deposited
If the balance exceeds a configured threshold then a withdrawal happens, placing the excess money in a savings account
The savings account is another service, only accessible during bank-hours, so if we are outside of bank hours, save the transaction as pending for later processing

### Identified Cases:
* HP-1: Deposit yields balance < configured amount. No side-effects
* HP-2: Deposit yields balance > configured amount. Transfer funds successfully
* HP-3: Deposit yields balance > configured amount outside bank hours. Transfer funds is pending.
* EP-1: Savings service unavailable
* EP-2: Transaction outside of bank hours

### Assumptions:
* Assume all tz are localtime for simplicity

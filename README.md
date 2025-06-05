Concurrency exercises
Lab 4: Basic conrurrency
Lab 5: Peterson 
Lab 7: Monitors & locks (EXAM)

# Checklist for converting monitors into locks:
### 1. remove "synchronized"
### 2. set all variables as @volatile
### 3. create a lock  ->   private val lock = new ReentrantLock(true)
### 4. crate waiting rooms  ->  private val room = lock.newCondition()
### 5. in each cs function add:
     lock.lock()
     try ....
     finally
         lock.unlock()
### 6. change wait() for await()
### 7. change notify() for signal()

THAT'S IT !

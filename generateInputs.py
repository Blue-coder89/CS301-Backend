import random
import string

firstlevelthreads = 5
secondLevelthreads = 30
TRAIN_NUMS=["22517","04652"]
DEP_DATES = ["2022-11-01","2022-11-01"]
NUM_TRAINS = len(TRAIN_NUMS)
REQUEST_PER_USER_LIM=1400

def addInputFile(thread,pool):
    fileName= open("./Input/pool-{}-thread-{}".format(pool,thread)+"_input.txt","w")
    totalRows=random.randint(1,REQUEST_PER_USER_LIM)

    for i in range(totalRows):
        ticketsTotal = random.randint(1,6)
        passengerNames=[]
        for j in range(ticketsTotal):
            passengerNames.append("".join(random.choices(string.ascii_uppercase+string.ascii_lowercase,k=random.randint(1,16))))
        trainIndex=random.randint(0,NUM_TRAINS-1)
        choice= random.choice(['AC','SL'])
        fileName.write("{} {} {} {} {}\n".format(ticketsTotal,", ".join(passengerNames),TRAIN_NUMS[trainIndex],DEP_DATES[trainIndex],choice))
    fileName.write("#")
for userNumber in range(firstlevelthreads):
    addInputFile(userNumber+1,1)
    for i in range(secondLevelthreads):
        addInputFile(i+1,userNumber+2)
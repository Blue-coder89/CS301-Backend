import random
import string

NUM_USERS = 50
TRAIN_NUMS=["22517","04652"]
DEP_DATES = ["2022-11-01","2022-11-01"]
NUM_TRAINS = len(TRAIN_NUMS)
REQUEST_PER_USER_LIM=200

def addInputFile(userNumber):
    fileName= open("./InputFiles/pool-1-thread-"+str(userNumber)+"_input.txt","w")
    totalRows=random.randint(1,REQUEST_PER_USER_LIM)

    for i in range(totalRows):
        ticketsTotal = random.randint(1,40)
        passengerNames=[]
        for j in range(ticketsTotal):
            passengerNames.append("".join(random.choices(string.ascii_uppercase+string.ascii_lowercase,k=random.randint(1,16))))
        trainIndex=random.randint(0,NUM_TRAINS-1)
        choice= random.choice(['AC','SL'])
        fileName.write("{} {} {} {} {}\n".format(ticketsTotal,", ".join(passengerNames),TRAIN_NUMS[trainIndex],DEP_DATES[trainIndex],choice))
    fileName.write("#")
for userNumber in range(NUM_USERS):
    addInputFile(userNumber+1)
#!/usr/bin/python3

import random
import time
import datetime

random.seed()

#Generate data for practical date

num_pruebas = random.randrange(1000,1500)

def get_patient_id():
    return "patient"+str(random.randrange(0,50))

def get_pruebas_file():
    n = datetime.datetime.now()
    n = n.replace(microsecond=0)
    return "pruebas_"+str(n.timestamp())[0:-2]+".csv"

def get_random_date():
    dat = datetime.datetime.now()
    dat -= datetime.timedelta(days=random.randrange(1))
    dat -= datetime.timedelta(seconds=random.randrange(900,60*60*12))
    dstr = str(dat.date())
    dstr += "|"
    dstr += str(dat.time().hour)+":"+str(dat.time().minute)
    return dstr

TESTS = ["TEMPERATURA","temperatura","Temperatura",
        "LINFOCITOS","linfocitos","Linfocitos",
        "RCB","rcb","rCB"
        "Eosinófilos","EOSINÓFILOS","Eosinofilos",
        "monocitos","MONOCITOS","Monocitos",
        "Hematocrito","HEMATOCRITO",
        "leucocitos","LEUCOCITOS","Leucocitos",
        "neuTrófilos","NEUTROFILOS","Neutrófilos"]

def get_random_test():
    return random.choice(TESTS)

def get_random_value():
    return str(random.random()*100)


UNITS = ["%","ºC","mg/ml","M/ul","g/dL"]

def get_random_unit():
    return random.choice(UNITS)

i = 0
with open(get_pruebas_file(),'w') as f:
    while i < num_pruebas:
        f.write(get_patient_id())
        f.write("|")
        f.write(get_random_date())
        f.write("|")
        f.write(get_random_test())
        f.write("|")
        f.write(get_random_value())
        f.write("|")
        f.write(get_random_unit())
        f.write("\n")

        i += 1
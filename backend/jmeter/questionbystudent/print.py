
f = open("SubmissionData.csv", "a")
text= ""
for i in range(500):
    text+="676,ONHOLD,"
    text += str(i+1315) 
    text +=",,2"
    text +="\n"
f.write(text)
f.close()



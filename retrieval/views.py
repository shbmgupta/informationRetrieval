from django.shortcuts import render
from django.http import HttpResponse
from django.shortcuts import render, reverse, redirect
import os
import subprocess# Create your views here.
def index(request):
    if request.method == 'POST':
        query  = request.POST['enterquery']
        query=str(query)
        # process = Popen(['java','-jar','IR_Monument_Search.jar',"\""+query+"\""],stdout=PIPE,stderr=PIPE)
        # res=[]
        # print(process.stdout.read())
        # x=subprocess.call(['java','-jar','IR_Monument_Search.jar',"mahal"])
        os.system("java -jar IR_Monument_Search.jar \""+query+"\" > results.txt")
        count=0
        results=[]
        with open("results.txt") as f:
	        count2=0
	        for i in f:
	        	count+=1
	        	if(count>3):
	        		if(count2%8==0):
	        			l=[]
	        			l.append(i)
	        		elif(count2%8==1):
	        			l.append(i)
	        		elif(count2%8==2):
	        			l.append(i)
	        		elif(count2%8==3):
	        			l.append(i)
	        		elif(count2%8==4):
	        			l.append(i)
	        		elif(count2%8==5):
	        			l.append(i)
	        		elif(count2%8==6):
	        			l.append(i)
	        		elif(count2%8==7):
	        			l.append(i)
	        			results.append(l)
	        		count2+=1
	        for i in results:
	        	print(i)


        # print ("query:-",query)
        context = {}
        context['title'] = query
        context['results']=results
        return render(request, 'retrieval/index.html',context)
    return render(request, 'retrieval/index.html')

from django.shortcuts import render
from django.http import HttpResponse
from django.shortcuts import render, reverse, redirect
# Create your views here.
def index(request):
    if request.method == 'POST':
        query  = request.POST['enterquery']
        print (query)
        context = {}
        context['result'] = query
        return render(request, 'retrieval/index.html',context)
    return render(request, 'retrieval/index.html')

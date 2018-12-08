from . import views
from django.urls import path
app_name = 'retrieval'

urlpatterns = [
    path('', views.index, name = "index"),
]

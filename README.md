
------------------------------------------------------------------------------------  
  
<h1 align="center">The Big Send</h1>  
<h2 align="center">A secure and easy bulk message sender</h2>  
<p align="center">  
    <img src="https://raster.shields.io/badge/Using-Flutter-blue?style=for-the-badge" alt="Using flutter"/>  
    <img src="https://raster.shields.io/tokei/lines/github/laspruca/thebigsend?style=for-the-badge" alt="Tetriversal logo"/>  
    <img src="https://raster.shields.io/github/license/laspruca/thebigsend?style=for-the-badge" alt="Tetriversal logo"/>  
</p>  
  
------------------------------------------------------------------------------------  
  
# 🤔 What is it?  
The Big Send is a an application designed to allow for people to send a large number 
of text messages to diffrent clients , by imoprting a csv file into the app.

It has merge capibillity, such that if the CSV file has a header, it will take the 
header and use the header names as refrence for you to embed them into the message. 
For example if you had the following CSV:
```csv
phone,name
12345,Foo
23456,Bar
```
You could construct personalized messages like a so
```
Hello {{name}}
```
  
# 🤷 Why?  
This was originally a project for my science fair during 2020, which I was able to 
win a more minor award in my age group.

It was brought to my attention by someone who wanted to send out a large number of 
text messages securely and cheaply. The problem for him and his organization was that 
security had to take precedence over cost, because the contents of these messages was 
confidential. This lead to him asking for this app to be developed.

Because of the type of application that was being built, I expanded its use case to 
include club managers and other people who would require such functionality, because 
I saw that this could be used in these situations.
  
# 📘 How it works  
The Big Send is an application originally written in the Android SDK, but move over 
to Flutter, for easier design and simpler porting to iOS.

The app has two main screens, a message screen, and a list screen
![The main app screen](main_screen.png) ![The Numbers screen](numbers_screen.png)

From the numbers screen the user can choose to import a csv file or from google 
drive, which is not yet implemented. The user will then be prompted on wether their 
CSV file has a header, if they answer yes, then it will select the column that 
contains the phone numbers. If they answer no, it will just import the fist column as 
phone numbers. It will then import it with the name Untitled x,  where x is just the 
next available name. The user can change the name of the list or delete it 
altogether. Once the click select, they will taken back to the main screen where they
 can input a message and click send to launch the messages.
  
# 🛠️ Building
Before building the application, the json parsers need to be built. This can be done
one of two ways:
1. One time build
```shell
flutter pub run build_runner
```
2. In watch mode
```shell
flutter pub run build_runner watch
```

After this the app can be built normally.
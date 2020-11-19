import 'package:flutter/material.dart';
import 'package:flutter/scheduler.dart';
import 'package:flutter/services.dart';
import 'package:the_big_send/widgets/main_screen.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    var brightness = SchedulerBinding.instance.window.platformBrightness;
    bool darkModeOn = brightness == Brightness.dark;
    return MaterialApp(
      title: 'The Big Send',
      theme: ThemeData(
        primarySwatch: Colors.blue,
        colorScheme: darkModeOn ? ColorScheme.dark() : ColorScheme.fromSwatch(),
        accentColor: Colors.blueAccent,
        buttonTheme: ButtonThemeData(
            colorScheme:
                darkModeOn ? ColorScheme.dark() : ColorScheme.fromSwatch()),
        visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
      home: MainScreen(),
    );
  }
}

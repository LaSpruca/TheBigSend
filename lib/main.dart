import 'package:flutter/material.dart';
import 'package:flutter/scheduler.dart';
import 'package:flutter/services.dart';
import 'package:flutter_redux/flutter_redux.dart';
import 'package:redux/redux.dart';
import 'package:the_big_send/state.dart';
import 'package:the_big_send/util/numbers.dart';
import 'package:the_big_send/widgets/main_screen.dart';
import 'package:the_big_send/widgets/numbrts_screen.dart';

void main() {
  runApp(RootWidget());
}

class RootWidget extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    var brightness = SchedulerBinding.instance.window.platformBrightness;
    bool darkModeOn = brightness == Brightness.dark;
    return StoreProvider<AppState>(
        store: store,
        child: MaterialApp(
          title: 'The Big Send',
          theme: ThemeData(
            primarySwatch: Colors.blue,
            colorScheme:
                darkModeOn ? ColorScheme.dark() : ColorScheme.fromSwatch(),
            accentColor: Colors.blueAccent,
            buttonTheme: ButtonThemeData(
                colorScheme:
                    darkModeOn ? ColorScheme.dark() : ColorScheme.fromSwatch()),
            visualDensity: VisualDensity.adaptivePlatformDensity,
          ),
          routes: {
            '/': (context) => MainPage(),
            '/numbers': (context) => NumbersPage()
          },
          initialRoute: '/',
        ));
  }
}

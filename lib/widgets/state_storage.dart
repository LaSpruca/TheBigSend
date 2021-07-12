import 'dart:async';
import 'dart:convert';

import 'package:flutter/cupertino.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:the_big_send/state.dart';

class StateStorage extends StatefulWidget {
  final Widget child;

  const StateStorage(this.child, {Key? key}) : super(key: key);

  @override
  _StateStorageState createState() => _StateStorageState();
}

class _StateStorageState extends State<StateStorage> {
  late StreamSubscription<AppState> _listener;

  void setupPrefs() async {
    var prefs = await SharedPreferences.getInstance();
    setState(() {
      _listener = store.onChange.listen((event) async {
        print("Saving state to storage: ${event.toJson()}");

        var json = jsonEncode(event.toJson());

        prefs.setString("state", json);
      });
    });

    String? stored = prefs.getString("state");

    if (stored != null) {
      print("Loading state from storage $stored");
      var decoded = jsonDecode(stored);
      print("$decoded  ${decoded.runtimeType}");
      store.dispatch(SetAppState(AppState.fromJson(decoded)));
    }
  }

  @override
  void initState() {
    setupPrefs();

    super.initState();
  }

  @override
  void dispose() {
    _listener.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return widget.child;
  }
}

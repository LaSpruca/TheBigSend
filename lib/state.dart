import 'dart:collection';

import 'package:redux/redux.dart';
import 'package:the_big_send/util/numbers.dart';

class AppState {
  Map<String, List<PhoneNumber>> numbersLists = HashMap();

  AppState(this.numbersLists);

  AppState.fromAppState(AppState another) {
    numbersLists = another.numbersLists;
  }
}

class AddList {
  String listName;
  List<PhoneNumber> numbers;

  AddList(this.listName, this.numbers);
}

AppState reducer(AppState ogState, dynamic payload) {
  switch (payload.runtimeType) {
    case AddList:
      var data = payload as AddList;
      ogState.numbersLists[data.listName] = data.numbers;
      return ogState;
    default:
      return ogState;
  }
}

final Store<AppState> store = Store(reducer, initialState: AppState(Map()));

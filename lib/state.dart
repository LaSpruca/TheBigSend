import 'package:redux/redux.dart';
import 'package:the_big_send/util/numbers.dart';

class AppState {
  Map<String, List<PhoneNumber>> numbersLists;

  AppState(this.numbersLists);

  AppState.fromAppState(AppState another) {
    numbersLists = another.numbersLists;
  }
}

class NumbersList {
  Map<String, List<PhoneNumber>> payload;
}

AppState reducer(AppState prev, dynamic action) {
  AppState newState = AppState.fromAppState(prev);
  switch (action.runtimeType) {
    case NumbersList:
      newState.numbersLists = action.payload;
      break;
    default:
      print("Invalid action");
      break;
  }
  return newState;
}

final Store<AppState> store = Store(reducer, initialState: AppState(Map()));

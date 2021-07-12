import 'dart:collection';

import 'package:json_annotation/json_annotation.dart';
import 'package:redux/redux.dart';
import 'package:the_big_send/util/numbers.dart';

part 'state.g.dart';

@JsonSerializable()
class AppState {
  Map<String, List<PhoneNumber>> numbersLists = HashMap();
  int count = 0;

  AppState(this.numbersLists, this.count);

  AppState.fromAppState(AppState another) {
    numbersLists = another.numbersLists;
  }

  factory AppState.fromJson(Map<String, dynamic> json) =>
      _$AppStateFromJson(json);

  Map<String, dynamic> toJson() => _$AppStateToJson(this);
}

class AddList {
  String listName;
  List<PhoneNumber> numbers;

  AddList(this.listName, this.numbers);
}

class RemoveList {
  String listName;

  RemoveList(this.listName);
}

class ChangeListName {
  String oldName;
  String newName;

  ChangeListName(this.oldName, this.newName);
}

class SetAppState {
  AppState state;

  SetAppState(this.state);
}

AppState reducer(AppState ogState, dynamic payload) {
  switch (payload.runtimeType) {
    case AddList:
      var data = payload as AddList;
      ogState.numbersLists[data.listName] = data.numbers;
      ogState.count++;
      return ogState;
    case RemoveList:
      var data = payload as RemoveList;
      ogState.numbersLists.remove(data.listName);
      return ogState;
    case ChangeListName:
      var data = payload as ChangeListName;
      var list = ogState.numbersLists[data.oldName]!;
      ogState.numbersLists.remove(data.oldName);
      ogState.numbersLists[data.newName] = list;
      return ogState;
    case SetAppState:
      var data = payload as SetAppState;
      return data.state;
    default:
      return ogState;
  }
}

final Store<AppState> store = Store(reducer, initialState: AppState(Map(), 0));

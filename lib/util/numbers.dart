import 'dart:collection';

import 'package:json_annotation/json_annotation.dart';

part 'numbers.g.dart';

@JsonSerializable()
class PhoneNumber {
  String number;
  Map<String, String> _mergeData = HashMap();

  PhoneNumber(this.number);

  PhoneNumber.withMergeData(this.number, this._mergeData);

  bool hasMergeData() => _mergeData.isNotEmpty;

  String processMessage(String message) {
    print(_mergeData);
    var splits = message
        .split("{{")
        .map((val) => val.split("}}"))
        .expand((i) => i)
        .map((e) => e.trim())
        .toList();
    for (var i in splits) {
      print("\"$i\"");
    }
    for (var key in _mergeData.keys) {
      var index = splits.indexOf(key);
      print("\"$key\"");
      if (index >= 0) {
        if (_mergeData[key] != null) {
          splits[index] = _mergeData[key]!;
        }
      }
    }
    return splits.join(" ");
  }

  factory PhoneNumber.fromJson(Map<String, dynamic> json) =>
      _$PhoneNumberFromJson(json);

  Map<String, dynamic> toJson() => _$PhoneNumberToJson(this);

  @override
  String toString() {
    return "{number: $number, mergeData: $_mergeData}";
  }
}

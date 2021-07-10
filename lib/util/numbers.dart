import 'dart:collection';

class PhoneNumber {
  String number;
  Map<String, String> _mergeData = HashMap();

  PhoneNumber(this.number);

  PhoneNumber.withMergeData(this.number, this._mergeData);

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
}

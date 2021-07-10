bool inNumber(String value) {
  RegExp regex = RegExp(r"(\+)?[0-9]+", caseSensitive: false, multiLine: false);
  return regex.hasMatch(value);
}

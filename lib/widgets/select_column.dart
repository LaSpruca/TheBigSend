import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:the_big_send/util/constants.dart';

class SelectColumn extends StatefulWidget {
  final List<String> headers;

  const SelectColumn(this.headers, {Key? key}) : super(key: key);

  @override
  _SelectColumnState createState() => _SelectColumnState();
}

class _SelectColumnState extends State<SelectColumn> {
  String dropdownValue = "";

  @override
  void initState() {
    dropdownValue = widget.headers[0];

    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: Text("Phone number column select"),
        ),
        body: Padding(
            padding: EdgeInsets.fromLTRB(50, 150, 50, 0),
            child: Column(children: [
              Padding(
                  padding: EdgeInsets.all(20),
                  child: Text(
                    "Please select the phone number column containing the phone numbers",
                    textAlign: TextAlign.center,
                    style: normal,
                  )),
              DropdownButton<String>(
                value: dropdownValue,
                icon: const Icon(Icons.arrow_downward),
                iconSize: 24,
                elevation: 16,
                underline: Container(
                  height: 2,
                  color: Colors.deepPurpleAccent,
                ),
                onChanged: (String? newValue) {
                  setState(() {
                    dropdownValue = newValue!;
                  });
                },
                items: widget.headers
                    .map<DropdownMenuItem<String>>((String value) {
                  return DropdownMenuItem<String>(
                    value: value,
                    child: Text(value),
                  );
                }).toList(),
              ),
              ElevatedButton(
                  onPressed: () => Navigator.pop(context, dropdownValue),
                  child: Text("Ok"))
            ])));
  }
}

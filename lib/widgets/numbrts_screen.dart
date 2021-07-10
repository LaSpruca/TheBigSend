import 'dart:io';

import 'package:csv/csv.dart';
import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:flutter_keyboard_visibility/flutter_keyboard_visibility.dart';
import 'package:the_big_send/util/constants.dart';
import 'package:the_big_send/util/util.dart';

class NumbersPage extends StatefulWidget {
  @override
  _NumbersPageState createState() => _NumbersPageState();
}

class _NumbersPageState extends State<NumbersPage> {
  bool kbVisable = false;

  @override
  Widget build(BuildContext context) {
    var height = MediaQuery.of(context).size.height;
    var padding = MediaQuery.of(context).padding;
    return Scaffold(
        appBar: AppBar(
          title: Text("The Big Send"),
        ),
        body: KeyboardVisibilityBuilder(
            builder: (context, kbVisable) => SingleChildScrollView(
                child: _build(context),
                physics:
                    kbVisable || height - padding.top - padding.bottom < 700
                        ? null
                        : NeverScrollableScrollPhysics())));
  }

  Future openFile(BuildContext context) async {
    var result = await FilePicker.platform
        .pickFiles(type: FileType.custom, allowedExtensions: ["text/csv"]);

    if (result == null) {
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(
        content: Text("File picker failed"),
      ));
    } else {
      var file = File(result.files.single.path!);
      var str = await file.readAsString();

      print(str);

      List<List<dynamic>> data = CsvToListConverter().convert(str);

      if (data.isEmpty) {
        return Future.value();
      }

      if (data[0].isEmpty) {
        return Future.value();
      }

      if (inNumber(data[0].toString())) {}
    }
  }

  @override
  Widget _build(BuildContext content) {
    return Column(
      children: [
        Padding(
          padding: EdgeInsets.all(24),
          child: Card(
              child: Padding(
                  padding: EdgeInsets.all(24),
                  child: Column(
                    children: [
                      Center(
                        child: Text(
                          "Numbers",
                          style: sectionHeading,
                        ),
                      ),
                      Center(
                        child: Text(
                          "Import from:",
                          style: sectionSubheading,
                        ),
                      ),
                      Row(
                        children: [
                          ElevatedButton(
                              onPressed: () => openFile(context),
                              child: Text(
                                "CSV FILE",
                                style: normalBold,
                              )),
                          ElevatedButton(
                              onPressed: () => {
                                    showDialog(
                                        context: context,
                                        builder: (context) => AlertDialog(
                                              title: Text('Future Feature'),
                                              content: SingleChildScrollView(
                                                child: Text(
                                                  "Currently not available",
                                                  style: normal,
                                                ),
                                              ),
                                              actions: <Widget>[
                                                TextButton(
                                                  child: Text(
                                                    'Ok',
                                                    style: TextStyle(
                                                        fontWeight:
                                                            FontWeight.bold),
                                                  ),
                                                  onPressed: () {
                                                    Navigator.of(context).pop();
                                                  },
                                                ),
                                              ],
                                            ))
                                  },
                              child: Text(
                                "GOOGLE DRIVE",
                                style: normalBold,
                              ))
                        ],
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                      )
                    ]
                        .map((e) => !(e is Row)
                            ? Padding(
                                padding: EdgeInsets.all(16),
                                child: e,
                              )
                            : Padding(
                                padding: EdgeInsets.fromLTRB(0, 0, 0, 16),
                                child: e,
                              ))
                        .toList(),
                  ))),
        ),
        Padding(
          padding: EdgeInsets.all(24),
          child: Card(
              child: Padding(
                  padding: EdgeInsets.all(24),
                  child: Column(children: [
                    Padding(
                        padding: EdgeInsets.all(16),
                        child: Center(
                          child: Text(
                            "Message",
                            style: sectionHeading,
                          ),
                        )),
                    Padding(
                        padding: EdgeInsets.fromLTRB(0.0, 16, 0.0, 16),
                        child: TextField(
                          obscureText: false,
                          keyboardType: TextInputType.multiline,
                          maxLines: null,
                          style: normal,
                          decoration: InputDecoration(
                            contentPadding:
                                EdgeInsets.fromLTRB(5.0, 15.0, 20.0, 5.0),
                            hintText: "Message ",
                          ),
                        ))
                  ]))),
        )
      ],
      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
    );
  }
}

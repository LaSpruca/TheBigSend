import 'dart:collection';
import 'dart:io';

import 'package:csv/csv.dart';
import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:flutter_keyboard_visibility/flutter_keyboard_visibility.dart';
import 'package:flutter_redux/flutter_redux.dart';
import 'package:the_big_send/state.dart';
import 'package:the_big_send/util/constants.dart';
import 'package:the_big_send/util/numbers.dart';
import 'package:the_big_send/util/util.dart';
import 'package:the_big_send/widgets/select_column.dart';

class NumbersPage extends StatefulWidget {
  @override
  _NumbersPageState createState() => _NumbersPageState();
}

class _NumbersPageState extends State<NumbersPage> {
  String listName = "";
  List<String> listNames = store.state.numbersLists.keys.toList().isEmpty
      ? [""]
      : store.state.numbersLists.keys.toList();

  @override
  void initState() {
    listName = listNames[0];
    store.onChange.listen((state) {
      setState(() {
        listNames = state.numbersLists.keys.toList();

        if (!listNames.contains(listName)) {
          setState(() {
            listName = listNames[0];
          });
        }
      });
    });
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    print("Building widget");
    if (listName != "") {
      print(store.state.numbersLists[listName]);
    }
    var height = MediaQuery.of(context).size.height;
    var padding = MediaQuery.of(context).padding;
    return Scaffold(
        appBar: AppBar(
          title: Text("The Big Send"),
        ),
        body: KeyboardVisibilityBuilder(
            builder: (context, kbVisible) => SingleChildScrollView(
                child: _build(context),
                physics:
                    kbVisible || height - padding.top - padding.bottom < 700
                        ? null
                        : NeverScrollableScrollPhysics())));
  }

  Future openFile(BuildContext context) async {
    var result = await FilePicker.platform
        .pickFiles(type: FileType.custom, allowedExtensions: ["csv"]);

    if (result == null) {
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(
        content: Text("File picker failed"),
      ));
    } else {
      var file = File(result.files.single.path!);
      var str = await file.readAsString();

      str = str.replaceAll("\n", "\r\n");

      List<List<String>> data = CsvToListConverter()
          .convert(str)
          .map((e) => e.map((e) => e.toString()).toList())
          .toList();

      if (data.isEmpty) {
        return Future.value();
      }

      if (data[0].isEmpty) {
        return Future.value();
      }

      var header = await showDialog<bool>(
          context: context,
          builder: (BuildContext context) {
            return AlertDialog(
              title: const Text('Header'),
              content: SingleChildScrollView(
                child: Text(
                  "Does you csv have a header?",
                  style: normal,
                ),
              ),
              actions: <Widget>[
                SimpleDialogOption(
                  onPressed: () {
                    Navigator.pop(context, true);
                  },
                  child: const Text('Yes'),
                ),
                SimpleDialogOption(
                  onPressed: () {
                    Navigator.pop(context, false);
                  },
                  child: const Text('No'),
                ),
              ],
            );
          });

      print(data);

      List<PhoneNumber> numbers = [];

      if (header == true) {
        var headers = data[0];
        data.removeAt(0);
        var phoneNoCol = await Navigator.push(context,
            MaterialPageRoute(builder: (context) => SelectColumn(headers)));
        print(phoneNoCol);
        var phoneNoColIndex = headers.indexOf(phoneNoCol);

        data = data.where((e) => isNumber(e[phoneNoColIndex])).toList();

        for (int i = 0; i < data.length; i++) {
          var record = data[i];
          var number = new HashMap<String, String>();
          for (int ii = 0; i < record.length; i++) {
            if (ii != phoneNoColIndex) {
              number[headers[ii]] = record[ii];
            }
          }
          numbers
              .add(PhoneNumber.withMergeData(record[phoneNoColIndex], number));
        }
      } else {
        numbers = data
            .where((element) => isNumber(element[0]))
            .map((e) => PhoneNumber(e[0]))
            .toList();
      }

      store.dispatch(AddList("Untitled ${store.state.count}", numbers));
    }
  }

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
                            "Select List",
                            style: sectionHeading,
                          ),
                        )),
                    Padding(
                        padding: EdgeInsets.fromLTRB(0.0, 16, 0.0, 16),
                        child: DropdownButton<String>(
                          value: listName,
                          icon: const Icon(Icons.arrow_downward),
                          iconSize: 24,
                          elevation: 16,
                          underline: Container(
                            height: 2,
                            color: Colors.deepPurpleAccent,
                          ),
                          onChanged: (String? newValue) {
                            setState(() {
                              listName = newValue!;
                            });
                          },
                          items: listNames
                              .map<DropdownMenuItem<String>>((String value) {
                            return DropdownMenuItem<String>(
                              value: value,
                              child: Text(value),
                            );
                          }).toList(),
                        ))
                  ]))),
        )
      ],
      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
    );
  }
}

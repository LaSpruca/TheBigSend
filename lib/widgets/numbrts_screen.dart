import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:flutter_keyboard_visibility/flutter_keyboard_visibility.dart';
import 'package:the_big_send/util/constants.dart';

class NumbersPage extends StatefulWidget {
  @override
  _NumbersPageState createState() => _NumbersPageState();
}

class _NumbersPageState extends State<NumbersPage> {
  bool kbVisable = false;

  @override
  void initState() {
    KeyboardVisibility.onChange
        .listen((kbState) => setState(() => {kbVisable = kbState}));
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    var height = MediaQuery.of(context).size.height;
    var padding = MediaQuery.of(context).padding;
    print(height - padding.top - padding.bottom);
    return Scaffold(
        appBar: AppBar(
          title: Text("The Big Send"),
        ),
        body: SingleChildScrollView(
            child: _build(context),
            physics: kbVisable || height - padding.top - padding.bottom < 700
                ? null
                : NeverScrollableScrollPhysics()));
  }

  void openFile() {
    FilePicker.platform
        .pickFiles(type: FileType.any)
        .then((value) => {print(value)});
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
                          RaisedButton(
                              onPressed: () => openFile(),
                              child: Text(
                                "CSV FILE",
                                style: normalBold,
                              )),
                          RaisedButton(
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

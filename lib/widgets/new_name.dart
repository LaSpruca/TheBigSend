import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:the_big_send/util/constants.dart';

class NewListName extends StatefulWidget {
  final String oldName;

  const NewListName(this.oldName, {Key? key}) : super(key: key);

  @override
  _NewListNameState createState() => _NewListNameState();
}

class _NewListNameState extends State<NewListName> {
  late String newName;
  late TextEditingController _controller;

  @override
  void initState() {
    newName = widget.oldName;
    _controller = TextEditingController();
    _controller.addListener(() {
      newName = _controller.text;
    });

    super.initState();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: Text("New name"),
        ),
        body: Padding(
            padding: EdgeInsets.fromLTRB(50, 150, 50, 0),
            child: Column(children: [
              Padding(
                  padding: EdgeInsets.all(20),
                  child: Text(
                    "Please enter the new name for ${widget.oldName}",
                    textAlign: TextAlign.center,
                    style: normal,
                  )),
              Padding(
                  padding: EdgeInsets.all(20),
                  child: TextField(
                    obscureText: false,
                    keyboardType: TextInputType.name,
                    maxLines: null,
                    style: normal,
                    decoration: InputDecoration(
                      contentPadding: EdgeInsets.fromLTRB(5.0, 15.0, 20.0, 5.0),
                      hintText: widget.oldName,
                    ),
                    controller: _controller,
                  )),
              ElevatedButton(
                  onPressed: () => Navigator.pop(context, newName),
                  child: Text("Ok"))
            ])));
  }
}

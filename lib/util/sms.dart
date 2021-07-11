import 'package:flutter/services.dart';

const _platform = const MethodChannel('theBigSend.laspruca.nz/sms');

Future<void> sendSMS(
    {required String message, required List<String> recipients}) async {
  try {
    await _platform.invokeMethod('sendSMS',
        <String, dynamic>{"message": message, "recipients": recipients});
  } on PlatformException catch (e) {
    throw Exception("Failed to send message: '${e.message}'.");
  }
}

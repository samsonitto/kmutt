# Food Volume Estimation Using a Smartphone
## Description
Being able to estimate food volume using a mobile phone camera is essential in estimating the quantity intake of that food. Such an endeavor is particularly helpful for caregivers of the diabetes mellitus (DM) or pre-DM patients to remotely monitor daily food intakes either so that insulin could be properly administered or as a preventative measure for the pre-DM patients. While algorithms exist, they have not yet taken full advantages of the current state of advance of the mobile technology. Besides none has been designed specifically for Thai food which could have different characteristics. In this project, the trainee will do a literature review on existing state of the art techniques for estimating food volume, summarizing the key findings as well as identifying those features that have now been supported by the mobile phone (i.e. Android and iOS). Based on such findings, the trainee recommends a conceptual design of a method for estimating Thai food volume using a mobile phone camera.

## [Serial Monitor App](SummerSerialMonitor-master/)

<img src="links/SerialMonitorApp.jpg" alt="Linear Movement App" width="400">

Serial Monitor App was created to delegate Time of Flight readings from Serial Monitor to the Android app. The App is using physicaloid library.

## [Linear Movement Measurement App](LinearDistanceMeasurement/)

<img src="links/LinearMovementApp.jpg" alt="Linear Movement App" width="400">

The Linear Movement App was created to show how inaccurate XY movement calculations are when you integrate accelerometer data twice. 

## [Arduino IDE Code](esp32/)

Arduino IDE code, that uses the VL53L0X library to get the Time of Flight readings in millimeters and the MedianFilter library to reduce the noise of these readings.

## [Final Report](links/Food_volume_measurement_app.docx)
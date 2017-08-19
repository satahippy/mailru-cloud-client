# Mail.ru Cloud Java Client

## Usage

```java
Cloud cloud = Cloud.Factory.instance();
cloud.login("you@mail.ru", "your-password");

// List directory content
System.out.println(cloud.getFolder("/").execute().body());

// Create folder
System.out.println(cloud.addFolder("/test_folder").execute().body());

// Upload file
System.out.println(cloud.uploadFile("/test_small_file", "aaa".getBytes()).execute().body());

// Remove file/folder
System.out.println(cloud.removeFile("/test_folder").execute().body());
```

## Tests

Before tests running you need to copy `src/test/resources/config.properties.dist` to `config.properties` and specify your settings.

## Release

We're using Sonatype for releasing.

In order to make this work you need to specify some gradle properties in `~/.gradle/gradle.properties`.
You can find example of this file in resources.
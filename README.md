# Mail.ru Cloud Java Client

[![](https://jitpack.io/v/satahippy/mailru-cloud-client.svg)](https://jitpack.io/#satahippy/mailru-cloud-client)

## Usage

First of all you need to add dependency:

```
repositories {
    jcenter()
    maven { url "https://jitpack.io" }
}
dependencies {
    compile 'com.github.satahippy:mailru-cloud-client:0.1.7'
}
```

Then you can use library:

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

We use JitPack for releases.
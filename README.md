# PlayerStorageAPI
Allows storing to Redis/MongoDB or both if you choose! 
Our API quickly adds database support to your minecraft plugins.

## Installation

Use the package manager [Maven](https://maven.apache.org/) to add PlayerStorageAPI as a shaded dependency.

```xml
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>com.github.hidden1nin</groupId>
            <artifactId>PlayerStorageAPI</artifactId>
            <version>2.1-SNAPSHOT</version>
        </dependency>
    </dependencies>
```

## Usage

```java
import com.hiddentech.playerstorage.PlayerStorageAPI;

private PlayerStorageAPI playerStorageAPI;


public final class Example extends JavaPlugin {
    @Override
    public void onEnable() {
        //Grab an instance of the PlayerStorageAPI
        this.playerStorageAPI = PlayerStorageAPI.getInstance(this);
        
        //Register values you would like to store
        playerStorageAPI.registerValue("foo",0);
        playerStorageAPI.registerValue("bar","string default value");
        playerStorageAPI.registerValue("test",true);
    }
    
    public void fooBarEvent(FooEvent event){
        playerStorageAPI.set(event.getplayer().getUniqueId(),"foo",1);
    }
    
}


```

## Contact
Reach me on discord at Hidden1nin#9457

## License
[CC](https://creativecommons.org/)

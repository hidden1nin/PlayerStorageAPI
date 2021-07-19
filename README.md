# PlayerStorageAPI
Allows storing to Redis/MongoDB or both if you choose! 
Our API quickly adds database support to your minecraft plugins.
View our [Spigot Page](https://www.spigotmc.org/resources/player-storage-api-maven-version.94290/)

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

public final class Example extends JavaPlugin {

    private PlayerStorageAPI playerStorageAPI;
    
    @Override
    public void onEnable() {
        //Grab an instance of the PlayerStorageAPI
        this.playerStorageAPI = PlayerStorageAPI.getInstance(this);
        
        //Register values you would like to store
        playerStorageAPI.registerValue("foo",0);
        playerStorageAPI.registerValue("bar","string default value");
        playerStorageAPI.registerValue("test",true);
    }
    
    //This event fires when a players data is loaded when they join
    public void playerJoinEvent(PlayerDataLoad event){
        //Retrieve a stored value
        int value = playerStorageAPI.getInt(event.getplayer().getUniqueId(),"foo");
        
        //Update the stored value
        playerStorageAPI.set(event.getplayer().getUniqueId(),"foo",value+1);
    }
    
}


```
## Config
Once your plugin has started it will create a second config file inside your plugins folder,
```yml
Redis_Connection: change this!
Redis_Port: 12345
Redis_Password: and this too!
Redis_Data_Expire_After: 86400
Storage_Configuration: both
Mongo_DB_Name: PlayerStorageAPI
Mongo_DB_ConnectionString: Change Me Too!
```
You need to change the connections aswell as the passwords.
If you only need Redis functionality change
```yml
Storage_Configuration: both
```
to
```yml
Storage_Configuration: redis
```
or to use MongoDB
```yml
Storage_Configuration: mongo
```
If you would like to use Redis for caching and MongoDB for deep storage leave it set to both, and change the Expire Time to how long you would like data to stay cached for (in seconds).

## Contact
Reach me for support/questions on discord at Hidden1nin#9457

## License
[CC](https://creativecommons.org/)

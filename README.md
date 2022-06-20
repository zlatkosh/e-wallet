# General info
This is a **gradle-7.4.1**, **Java 17**, **Spring boot 2.7.0** REST Server application.

## REST API endpoints 
The **e-wallet** application exposes the following endpoints:  
 - [Login endpoint](#login-endpoint)  
 - [Create wallet API](#create-wallet-api) 
 - [Create Play Session API](#create-wallet-api) 
 - [Create Increase Balance Transaction API](#create-increase-balance-transaction-api) 
 - [Create Decrease Balance Transaction API](#create-decrease-balance-transaction-api) 
 - [Get User Session/Transaction History API](#get-user-sessiontransaction-history-api) 

[e-wallet.postman_collection.json](server/src/test/resources/e-wallet.postman_collection.json): Is the exported  Postman project I used to test this application locally.

A Swagger UI is available with mapping **/swagger-ui/index.html**. 
The Swagger UI however does not expose the login and refresh_token endpoints.  

### Login endpoint  
We are using a very basic login POST endpoint taking HTTP parameters **username** and **password** as input (sample request below).
``` http request
POST http://localhost:9093/login?username=myUsername&password=myPassword
```
The login endpoint returns a JSON response containing JSON Web Token (JWT) type, access and refresh token. 
```json
{
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJyZWZyZXNoRHVyYXRpb24iOjEyM30.-gyh--a5k3LFQS4U2Lyjv_YTGge_y9na0dsszY0nm8A"
}
```
> **⚠ Warning**  
> All other APIs in this application can ONLY be accessed with a valid **accessToken** returned by this API.  
> They require for it to be provided in the Authorization header of their request as seen below!
> ```
> Authorization:Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
> ```
> Whenever the **accessToken** expires a call to endpoint `GET http://<host>:<port>/access/refresh_token` is needed, to get a new valid access token. 
> This call must have the login response **refreshToken** in the Authorization header as seen below:
> ```
> Authorization:Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJyZWZyZXNoRHVyYXRpb24iOjEyM30.-gyh--a5k3LFQS4U2Lyjv_YTGge_y9na0dsszY0nm8A
> ```
> The **refresh_token** reply will be the same type of JSON message as after calling the login endpoint, 
> it will also contain the same exact **refreshToken**, but it will return a new valid **accessToken** for other APIs to use.  
> **After the refreshToken also expires a new call to the login endpoint is needed to get a new accessToken/refreshToken set**

The duration of the **accessToken** and **refreshToken** is configurable through the 2 parameters below found in [application.properties](server/src/main/resources/application.properties)
``` properties
#**spring security and JWT configuration**
#refresh token/play session expiry in milliseconds. Default is 60 minutes: 60*60*1000=3600000
app.security.expiry.refresh-token=3600000
#access token expiry in milliseconds. Default is 10 minutes: 10*60*1000=600000
app.security.expiry.access-token=600000
```
The secret used to encode both JWTs is hardcoded in constant [JwtUtility.SECRET_KEY](service/src/main/java/com/zlatkosh/ewallet/security/JwtUtility.java)

### Create wallet API
**PUT** request to API mapping **/wallet/create**  
This API is used to create a wallet for the logged-in User/Player. The limitation is that only a single wallet can exist for any given user.   
The only thing this API needs is the **accessToken** provided by the [Login endpoint](#login-endpoint). 
It takes the username of the user for whom the wallet will be created from the decoded **accessToken**. 
The wallet is created with a balance of 0.00 in the currency of the DB locale. The wallet DB table is updated.

### Create Play Session API
**PUT** request to API mapping **/session/create**  
The session creation API - creates a “play session” for a player, a player can have multiple sessions.  
The only thing this API needs is the **accessToken** provided by the [login endpoint](#login-endpoint).  
A play session is automatically created upon a successful login and has the same expiration as the generated refreshToken.
Meaning a new play session is created with the next login after the refresh token has expired. The play_session DB table is updated.

### Create Increase Balance Transaction API
**PUT** request to API mapping **/tx/balance/increase**  
This API is used to create a transaction that will deposit funds to the player's wallet. 
A monetary transaction can only happen for an active Play session.  
The API expects a request containing a serialized version of 
[BalanceIncreaseDto.java](model/src/main/java/com/zlatkosh/ewallet/model/controller/BalanceIncreaseDto.java) (sample request below)
and the **accessToken** provided by the [login endpoint](#login-endpoint)
``` json
{
    "txType" : "DEPOSIT",
    "transactionAmount" : 1.23
}
```
 - txType can only have **DEPOSIT** or **WIN** as a value for this API
 - transactionAmount must be a positive number with up to 2 decimal places  

The flow of events below is executed in a JDBC transaction:
 - The wallet table is updated by adding the **transactionAmount** value to the already existing wallet balance
 - the update returns the old and new value of **transactionAmount**
 - The transaction table is updated taking the values of the fields in [TransactionDto](model/src/main/java/com/zlatkosh/ewallet/model/controller/TransactionDto.java)  
> **Note:** a commit is only executed if of the above steps succeed. A rollback is performed by the JdbcTemplate otherwise.

### Create Decrease Balance Transaction API
**PUT** request to API mapping **/tx/balance/decrease**  
This API is used to create a transaction that will remove funds from the player's wallet.
A monetary transaction can only happen for an active Play session.  
The API expects a request containing a serialized version of
[BalanceDecreaseDto.java](model/src/main/java/com/zlatkosh/ewallet/model/controller/BalanceDecreaseDto.java) (sample request below)
and the **accessToken** provided by the [login endpoint](#login-endpoint)
``` json
{
    "txType" : "WITHDRAW",
    "transactionAmount" : 1.23
}
```
- txType can only have **WITHDRAW** or **BET** as a value for this API
- transactionAmount must be a positive number with up to 2 decimal places

The flow of events below is executed in a JDBC transaction:
- The wallet table is updated by removing the **transactionAmount** value from the already existing wallet balance
- If the wallet doesn't have sufficient funds for the transaction we throw this error message`"Bad request: Insufficient funds! You tried to take -1.23 from you wallet when your current balance is 0.00."`
- the update returns the old and new value of **transactionAmount**
- The transaction table is updated taking the values of the fields in [TransactionDto](model/src/main/java/com/zlatkosh/ewallet/model/controller/TransactionDto.java)
> **Note:** a commit is only executed if all of the above steps succeed. A rollback is performed by the JdbcTemplate otherwise.

### Get User Session/Transaction History API
**GET** request to API mapping **/history/transactions**
The only thing this API needs is the **accessToken** provided by the [login endpoint](#login-endpoint).  
This API returns a History of Play sessions and transactions for each one for the active user.  
The response is a serialized version of [UserDataDto](model/src/main/java/com/zlatkosh/ewallet/model/controller/UserDataDto.java). Sample response below:
``` json
{
    "username": "myuser",
    "playSessions": [
        {
            "sessionId": 10,
            "sessionStartTime": "2022-06-18T08:40:15.698+00:00",
            "sessionEndTime": "2022-06-18T09:40:15.693+00:00",
            "transactions": [
                {
                    "txId": 66,
                    "txType": "WIN",
                    "txTime": "2022-06-18T08:40:55.523+00:00",
                    "txAmount": 1.00,
                    "oldBalance": 0.00,
                    "newBalance": 1.00
                },
                {
                    "txId": 67,
                    "txType": "WITHDRAW",
                    "txTime": "2022-06-18T08:40:58.805+00:00",
                    "txAmount": -1.00,
                    "oldBalance": 1.00,
                    "newBalance": 0.00
                },
                {
                    "txId": 68,
                    "txType": "WIN",
                    "txTime": "2022-06-18T08:42:17.174+00:00",
                    "txAmount": 1.00,
                    "oldBalance": 0.00,
                    "newBalance": 1.00
                },
                {
                    "txId": 69,
                    "txType": "WITHDRAW",
                    "txTime": "2022-06-18T08:42:20.862+00:00",
                    "txAmount": -1.00,
                    "oldBalance": 1.00,
                    "newBalance": 0.00
                },
                {
                    "txId": 70,
                    "txType": "DEPOSIT",
                    "txTime": "2022-06-18T08:42:48.282+00:00",
                    "txAmount": 1.00,
                    "oldBalance": 0.00,
                    "newBalance": 1.00
                },
                {
                    "txId": 71,
                    "txType": "BET",
                    "txTime": "2022-06-18T08:42:56.954+00:00",
                    "txAmount": -1.00,
                    "oldBalance": 1.00,
                    "newBalance": 0.00
                }
            ]
        },
        {
            "sessionId": 11,
            "sessionStartTime": "2022-06-18T11:58:22.551+00:00",
            "sessionEndTime": "2022-06-18T12:58:22.534+00:00",
            "transactions": [
                {
                    "txId": 72,
                    "txType": "DEPOSIT",
                    "txTime": "2022-06-18T12:01:54.809+00:00",
                    "txAmount": 1.00,
                    "oldBalance": 0.00,
                    "newBalance": 1.00
                },
                {
                    "txId": 73,
                    "txType": "BET",
                    "txTime": "2022-06-18T12:02:04.529+00:00",
                    "txAmount": -1.00,
                    "oldBalance": 1.00,
                    "newBalance": 0.00
                }
            ]
        }
    ]
}
```

### Running the application locally
The application creates its own DB schema the first time it runs using [V1__init_db.sql](data_access/src/main/resources/db/migration/V1__init_db.sql) which gets executed by [FlyWay](https://flywaydb.org/documentation/usage/plugins/springboot). To do that however it requires knowledge of a few things.
 - [DB_datasource.properties](data_access/src/main/resources/DB_datasource.properties) contains the Postgres connection details the DataSource will need.  
 - [docker-compose.yaml](docker-compose.yaml) creates the Postgres database instance this application will use.  
Before the first run of the application it needs to be executed against a running local docker instance. This part can be skipped if you already have a running Postgres instance, 
but [DB_datasource.properties](data_access/src/main/resources/DB_datasource.properties) will need to be updated if that's the case.  
 - [application.properties](server/src/main/resources/application.properties) contains the following properties:
   - **server.port** used to configure the port on which the application will run
   - **app.security.expiry.access-token** used to define the duration of the generated **accessToken** in milliseconds
   - **app.security.expiry.refresh-token** used to define the duration of the generated **refreshToken** and Play Session in milliseconds

 - [logback.xml](server/src/main/resources/logback.xml) contains the Logback logging configuration

[EWalletServerApplication.java](server/src/main/java/com/zlatkosh/ewallet/EWalletServerApplication.java) is the main class and no additional configuration is needed to run the application from within an IDE like IntelliJ IDEA.

1. install docker
2. then you should go root project path
3. run docker-compose.yml (docker-compose up -d) wait for install after that run
4. enter this url in browser http://localhost:8080/
5. click Administration Console
6. in user and password by username= admin password= admin login
7. in dashboard left top side click combo box and Create realm
8. in create realm page fill Realm name (in this article you should add name (webApplication)
9. click to Groups menu in menubar and create these groups (admin, manger)
10. click Realms roles in menubar and create these roles(showUsers, createUser)
11. assign roles to group , click Groups menu and click your Groups and click Role mapping and click Assign role button and chose your roles for admin(showUsers, createUser) also for manger (showUsers)
12. Create Client, click to Clients menu and click to Create Client and fill Client ID by (myApp) and next button and enable Client authentication checkbox and next and save
13. if you could create client and then you can copy your client secret in Clients menu credentials tab in Client secret copy your secret and past in keycloak-config.properties file in project
14. go to client scope and click to Dedicated scopes  and click Add predefined mappers and select username and email and click add
15. copy public key in Realms settings click to RS256 in public key and copy in jwt-config.properties file 

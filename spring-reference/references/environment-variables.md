![image](https://github.com/user-attachments/assets/883b9d57-85ab-4e77-af4e-fd34d6fd54b3)![image](https://github.com/user-attachments/assets/54ea8869-9b27-4cdb-835b-fd8d9d9fd8c9)![image](https://github.com/user-attachments/assets/12b40e3b-bc77-469c-8471-2af69871d296)## Using Env variables of System and from Intellij idea from code and from properties:



- We have some predefined env variables of system and we will add in intellij idea as well

- Adding env variables from intellij go to run and select edit config :
 ![image](https://github.com/user-attachments/assets/ecd17086-5780-474e-8559-a4ef2129cef4)

- Select our project and go to the section of env variables and we can add there its self like key and value seperated by ; to next variables and we can also add inside clicking it.

  ![image](https://github.com/user-attachments/assets/ad27e93f-2188-4aa8-a5ed-9e643eb67ff3)
- Click on plus symbol add keys and values note this will be pointed to only our project and also we can see system env variables.

![image](https://github.com/user-attachments/assets/3bf12e39-c9d7-42e0-ad5f-9a640a29b4f3)
 - we have added these two variables: ```testUser=Ashfaq;testPassword=password```.

- ## Using the env values. flow -> app.props -> code and use
  
  ![image](https://github.com/user-attachments/assets/7b62fcf0-b43b-4b46-871c-568c90abe5cd)

 
 ```
  
@SpringBootApplication
public class RagDemoApplication
//		implements CommandLineRunner
{

//
//	@Value("${app.username}")
//	private String username;
//
//	@Value("${app.password}")
//	private String password;
//
//
//	@Value("${app.cName}")
//	private String cName;
//
//	public void printEnv() {
//		System.out.println("Username: " + username);
//		System.out.println("Password: " + password);
//		System.out.println("cName: " + cName);
//
//	}

void run(){
printEnv();
}

 /*
	o/p
	Username: Ashfaq
Password: password
cName: ASHU
	 */
```


## Directly Taking from the Code:

- We have already set the values which is above ones and we will use them but directly take from code.


```
  
@SpringBootApplication
public class RagDemoApplication
{

	public static void main(String[] args) {
		SpringApplication.run(RagDemoApplication.class, args);

//		testUser=Ashfaq;testPassword=password (intellij); COMPUTERNAME=ASHU(OS)

		String username = System.getenv("testUser");
		String password = System.getenv("testPassword");
		String cName = System.getenv("COMPUTERNAME");


		System.out.println("Username: " + username);
		System.out.println("Password: " + password);
		System.out.println("cName: " + cName);

		/*
O/P
		Username: Ashfaq
Password: password
cName: ASHU

		 */


	
	}
```


## Setting the values from code and using it:


```

@SpringBootApplication
public class RagDemoApplication
{
	public static void main(String[] args) {
		SpringApplication.run(RagDemoApplication.class, args);

		System.setProperty("custom.env", "myValue");
		String value = System.getProperty("custom.env");
		System.out.println("Custom Env: " + value);

// OP: 		Custom Env: myValue

	}


```

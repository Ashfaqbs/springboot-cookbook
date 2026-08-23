# SpringBoot-Jasypt-JavaSimplifiedEncryption

## This Springboot project is a demonstration of using JASYPT encryption for vulnerable data 


### Steps
Add the Jasypt dependency in pom.xml

    	<dependency>
			<groupId>com.github.ulisesbocchio</groupId>
			<artifactId>jasypt-spring-boot-starter</artifactId>
			<version>3.0.4</version>
		 </dependency>




Add the annotation @EnableEncryptableProperties on the main class 


     @SpringBootApplication
     @EnableEncryptableProperties
    public class SpringBootJasyptApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootJasyptApplication.class, args);
  	}

   }

Create a Encrypted value of the data/input which we want to encrypt by JAR , we have other ways too like online links , maven plugins but here we would be using a jasypt JAR 
 <a href=https://repo1.maven.org/maven2/org/jasypt/jasypt/1.9.3/jasypt-1.9.3.jar>JAR link<a>
        
        "C:\Users\Ashfaq PC\Desktop\Springboot\jasypt-SpringBoot\jasypt-1.9.3.jar"

once we have downloaded the jar we will open the jar in cmd/git bash and create encrypted value below are the eg:

     in CMD
    C:\Users\Ashfaq PC\Desktop\Springboot\jasypt-SpringBoot>java -cp jasypt-1.9.3.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI input=mysecretValue password=1234 
    algorithm=PBEWITHHMACSHA512ANDAES_256 ivGeneratorClassName=org.jasypt.iv.RandomIvGenerator

    ----ENVIRONMENT-----------------

    Runtime: Oracle Corporation Java HotSpot(TM) 64-Bit Server VM 19.0.1+10-21

    ----ARGUMENTS-------------------

    input: mysecretValue
    password: 1234
     ivGeneratorClassName: org.jasypt.iv.RandomIvGenerator
    algorithm: PBEWITHHMACSHA512ANDAES_256

    ----OUTPUT----------------------

      jxNXLTaGTfnRVgmcPa1ij/oZ6FSllewQ6iF7A4tCxnhBk+G1GViZggKnS9EU6oaJ

    in app.property

      mysecretKey=mysecretValue

     now we have to add like this 
    
     mysecretKey=ENC(jxNXLTaGTfnRVgmcPa1ij/oZ6FSllewQ6iF7A4tCxnhBk+G1GViZggKnS9EU6oaJ)


demo2

     mysecretKey1=mysecretValue1


    C:\Users\Ashfaq PC\Desktop\Springboot\jasypt-SpringBoot>java -cp jasypt-1.9.3.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI input=mysecretValue1 password=1234 
    algorithm=PBEWITHHMACSHA512ANDAES_256 ivGeneratorClassName=org.jasypt.iv.RandomIvGenerator


    C:\Users\Ashfaq PC\Desktop\Springboot\jasypt-SpringBoot>java -cp jasypt-1.9.3.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI input=mysecretValue1 password=1234 
    algorithm=PBEWITHHMACSHA512ANDAES_256 ivGeneratorClassName=org.jasypt.iv.RandomIvGenerator

     ----ENVIRONMENT-----------------

     Runtime: Oracle Corporation Java HotSpot(TM) 64-Bit Server VM 19.0.1+10-21



     ----ARGUMENTS-------------------

    input: mysecretValue1
    password: 1234
    ivGeneratorClassName: org.jasypt.iv.RandomIvGenerator
    algorithm: PBEWITHHMACSHA512ANDAES_256



    ----OUTPUT----------------------

    ghWx7LyJMyGYyVIzqx0DvPEbtgW5r2IQ4L1/i09gLxfp4vNOUREZMUJEF64sZfNQ



Doing with DB


     ##DB Config
    spring.datasource.url=jdbc:mysql://localhost:3306/springboot
    spring.datasource.username=root
    spring.datasource.password=admin
    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
    spring.jpa.hibernate.ddl-auto=update

    go to the downloaded jasypt-1.9.3.jar path


     java -cp jasypt-1.9.3.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI input=root password=1234 algorithm=PBEWITHHMACSHA512ANDAES_256 
     ivGeneratorClassName=org.jasypt.iv.RandomIvGenerator


     root
     C:\Users\Ashfaq PC\Desktop\Springboot\jasypt-SpringBoot>java -cp jasypt-1.9.3.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI input=root password=1234 
     algorithm=PBEWITHHMACSHA512ANDAES_256 ivGeneratorClassName=org.jasypt.iv.RandomIvGenerator

     ----ENVIRONMENT-----------------

    Runtime: Oracle Corporation Java HotSpot(TM) 64-Bit Server VM 19.0.1+10-21



      ----ARGUMENTS-------------------

     input: root
     password: 1234
     ivGeneratorClassName: org.jasypt.iv.RandomIvGenerator
    algorithm: PBEWITHHMACSHA512ANDAES_256



     ----OUTPUT----------------------

     KBx4B9sMhFNlU8Lgz7JUYLt6KY3ums/F2BmYjJAA3YQ+f7Ktxol20+4tYvPIUhvh


     admin
    java -cp jasypt-1.9.3.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI input=admin password=1234 algorithm=PBEWITHHMACSHA512ANDAES_256 
    ivGeneratorClassName=org.jasypt.iv.RandomIvGenerator
    C:\Users\Ashfaq PC\Desktop\Springboot\jasypt-SpringBoot>java -cp jasypt-1.9.3.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI input=admin password=1234 
    algorithm=PBEWITHHMACSHA512ANDAES_256 ivGeneratorClassName=org.jasypt.iv.RandomIvGenerator

    ----ENVIRONMENT-----------------

    Runtime: Oracle Corporation Java HotSpot(TM) 64-Bit Server VM 19.0.1+10-21



    ----ARGUMENTS-------------------

    input: admin
    password: 1234
    ivGeneratorClassName: org.jasypt.iv.RandomIvGenerator
    algorithm: PBEWITHHMACSHA512ANDAES_256

    ----OUTPUT----------------------

    TniwflEIJFH1hhsiCynqWx3y8AHptFGrqTHdKgWzITEVPmYIG0x+XtDCs08M3eHG

    C:\Users\Ashfaq PC\Desktop\Springboot\jasypt-SpringBoot>

    java -cp jasypt-1.9.3.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI input=admin password=ashfaq algorithm=PBEWITHHMACSHA512ANDAES_256 
    ivGeneratorClassName=org.jasypt.iv.RandomIvGenerator




Now let us see the command   java -cp jasypt-1.9.3.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI input=admin password=ashfaq algorithm=PBEWITHHMACSHA512ANDAES_256 
    ivGeneratorClassName=org.jasypt.iv.RandomIvGenerator

We are calling the jar passing input , and input is where we need to pass the value to be encrypted, eg: here input=admin i.e DB password , password in the command is a secret key which we have to give as this will be helping in encryption and decryption in runtime as well ,algorithm is PBEWITHHMACSHA512ANDAES_256 whihc is a default one and call the command 
a output will be generated eg:   TniwflEIJFH1hhsiCynqWx3y8AHptFGrqTHdKgWzITEVPmYIG0x+XtDCs08M3eHG and we need to pass this now in application.properties as ENC(TniwflEIJFH1hhsiCynqWx3y8AHptFGrqTHdKgWzITEVPmYIG0x+XtDCs08M3eHG) 

     spring.datasource.password=ENC(TniwflEIJFH1hhsiCynqWx3y8AHptFGrqTHdKgWzITEVPmYIG0x+XtDCs08M3eHG)

Now we have to tell the application the key as well that would be our password two ways 

1. In application.properties jasypt.encryptor.password=1234 OR

         jasypt.encryptor.password=1234
   
2. in VM argumnets of the application pass this -Djasypt.encryptor.password=1234
   ![VM-arg](https://github.com/DarkSharkAsh/SpringBoot-Jasypt-JavaSimplifiedEncryption/assets/105435085/555f26a4-7b6b-48d0-898a-aa104001ae72) apply and run


        -Djasypt.encryptor.password=1234

Run the application .

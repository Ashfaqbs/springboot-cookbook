# Spring-Ref


**Multipart File Uploads**
```yml
spring:
  servlet:
    multipart:
      enabled: true
      max-file-size: 100MB
      max-request-size: 100MB
```
- **multipart.enabled: true**: Enables support for multipart file uploads (e.g., images, documents).
- **max-file-size: 100MB**: Sets the maximum file size for uploaded files to 100 MB.
- **max-request-size: 100MB**: Sets the maximum request size (total of all files in one request) to 100 MB.

 The configurations we mentioned for the servlet multipart settings are related to handling file uploads in your Spring Boot application:

1. **Multipart Enabled**:
   - When we set `enabled: true`, it allows your application to handle multipart file uploads. This is essential for scenarios like uploading images, documents, or other files through a web form.

2. **Max File Size**:
   - The `max-file-size: 100MB` setting defines the maximum size for an individual file upload. If a user tries to upload a file larger than this size, the server will reject the request and throw an error. This helps prevent excessive use of server resources and storage.

3. **Max Request Size**:
   - The `max-request-size: 100MB` setting specifies the maximum size for the entire request, which includes all files and form data combined. If the total size exceeds this limit, the server will also reject the request. This is useful for managing the total amount of data sent to the server in a single request.

### Summary
These settings help control the size of uploads to protect your application from large, potentially harmful files and to manage server resources efficiently. If users exceed these limits, they will receive an error, prompting them to reduce the file size or split their uploads.



## SuppressWarnings

`@SuppressWarnings` is commonly used in a few scenarios. Let's go through some examples:

1. **Unused Variables:**
   ```java
   @SuppressWarnings("unused")
   public void myMethod() {
       int unusedVar = 0; // This will not trigger a warning now.
   }
   ```

2. **Deprecated Methods:**
   ```java
   @SuppressWarnings("deprecation")
   public void useDeprecatedMethod() {
       oldMethod(); // No warning for using a deprecated method.
   }
   ```

3. **Unchecked Casts:**
   ```java
   @SuppressWarnings("unchecked")
   public void addToList(Object obj) {
       List<String> list = new ArrayList<>();
       list.add((String) obj); // Suppresses unchecked cast warning.
   }
   ```

4. **Raw Types:**
   ```java
   @SuppressWarnings("rawtypes")
   public void useRawList() {
       List rawList = new ArrayList(); // Raw type warning suppressed.
   }
   ```

Developers use `@SuppressWarnings` to keep the code clean and maintain focus on important warnings. Just need remember to use it wisely and not to hide warnings that could indicate real issues!




## @Deprecated
The @Deprecated annotation! It's used in Java to indicate that a particular class, method, or field should no longer be used. This might be because it's outdated, has a better alternative, or will be removed in future versions. By marking something as deprecated, developers are warned to avoid using it and to look for an alternative solution.

You don't need to create this annotation yourself; it's provided by Java. When you see @Deprecated above a class, method, or field, it's a heads-up to steer clear of it in your code.

package com.example.todo.crypto;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * JPA AttributeConverter that transparently encrypts/decrypts String fields.
 *
 * Usage on an entity field:
 *   @Convert(converter = EncryptedStringConverter.class)
 *   private String title;
 *
 * The app code never sees ciphertext — getTitle() always returns plaintext.
 *
 * Spring Boot 3 + Hibernate 6 registers SpringBeanContainer automatically,
 * which allows @Autowired injection here even though Hibernate instantiates converters.
 */
@Converter
public class EncryptedStringConverter implements AttributeConverter<String, String> {

    // Field injection is required here: Hibernate 6 instantiates converters via
    // SpringBeanContainer (auto-configured by Spring Boot 3), which calls setters
    // but does NOT invoke constructors with parameters.
    @Autowired
    private EncryptionService encryptionService;

    /**
     * Called when writing to DB.
     * "buy milk" → "ENC:AgF+xK3mNp..."
     */
    @Override
    public String convertToDatabaseColumn(String plaintext) {
        return encryptionService.encrypt(plaintext);
    }

    /**
     * Called when reading from DB.
     * "ENC:AgF+xK3mNp..." → "buy milk"
     */
    @Override
    public String convertToEntityAttribute(String dbData) {
        return encryptionService.decrypt(dbData);
    }
}

# Uso de autenticação em Apps Android

Ligando com o servidor de autenticação do Google/Firebase.

## Configuração do Firebase
- criar o projeto no Firebase
- Registrar o app no Firebase (Configuração do projeto / seus aplicativos)
- baixar o arquivo `google-services.json` e colocar na pasta `app/`
- Ajustar o script do Gradle nível de projeto para usar o plugin do Firebase

```bash
buildscript {
  repositories {
    // Make sure that you have the following two repositories
    google()  // Google's Maven repository
    mavenCentral()  // Maven Central repository
  }
  dependencies {
    ...
    // Add the dependency for the Google services Gradle plugin
    classpath 'com.google.gms:google-services:4.3.15'
  }
}


```
- adicionar o SDK do Firebase ao projeto, o script Gradle no nível do módulo:app

```bash
dependencies {
  // ...

  // Import the Firebase BoM
  implementation platform('com.google.firebase:firebase-bom:32.0.0')

  // When using the BoM, you don't specify versions in Firebase library dependencies

  // Add the dependency for the Firebase SDK for Google Analytics
  //implementation 'com.google.firebase:firebase-analytics-ktx'

  implementation 'com.google.firebase:firebase-auth'
}

```
- Ahhhh! não se esqueça de "syncar" o projeto com o Gradle
- de volta ao Firebase, na aba "Authentication"
- adicionar Authentication
- Escolher os métodos de sign-in
- Pronto agora é programar!
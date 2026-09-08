# ¿Lleva Tilde?

**¿Lleva Tilde?** is a Spanish-language Android application designed to determine whether a Spanish word requires a written accent mark (*tilde*) over any of the vowels.

The application combines a **Spanish lexicon, Trie-based dictionary lookup, text normalization, and rule-based accentuation analysis** to evaluate user input. It is designed both as a practical reference tool and as a language-learning application for Spanish learners and Spanish users.

## Features

- Determines whether a Spanish word requires a written accent mark
- Accepts typed or spoken input
- Normalizes input before performing dictionary lookup
- Uses a Trie data structure for efficient lexicon searches
- Applies rule-based Spanish accentuation logic when necessary
- Identifies and returns the correctly accented form of known words
- Allows users to save previously searched words
- Provides access to additional definitions and linguistic information
- Includes an optional Claude Deep Dive for further exploration of a word

## How It Works

The application uses a hybrid rule-and-dictionary approach rather than a machine-learning classifier.

A simplified processing flow is:

```text
User Input
    ↓
Text Normalization
    ↓
Trie-Based Dictionary Search
    ↓
 ┌────────────────────┬────────────────────┐
 │ Word Found         │ Word Not Found     │
 │                    │                    │
 │ Return known       │ Apply Spanish      │
 │ accented form      │ accentuation rules │
 └────────────────────┴────────────────────┘
```


## Optional Claude Deep-Dive

After evaluating a word, the application provides an optional **Deep Dive** that launches a structured prompt in Claude for additional linguistic context, examples, and further research.

Claude is **not used to determine whether a word requires an accent mark**. The accent placement decision is made by the application's own dictionary, Trie lookup, and rule-based logic. 


## Technologies
- **Java**
- **Android Studio**
- **Android SDK**
- **Gradle**
- **JSON**
- **Trie data structure**
- **Speech-to-text**
- **Rule-based linguistic analysis**


## Project Structure 

The primary application logic is located under: llevatilde/src/main/java/

The active Spanish lexicon is stored in: llevatilde/src/main/assets/diccionario_tilde_json.json

### Key components include:
- **AccentAnalyzer** - applies Spanish accentuation analysis
- **PalabraTrie** and **TrieNode** - implement Trie-based lexicon lookup
- **LexiconLoader** - loads the application's Spanish lexicon
- **TextNormalizer** - prepares user input for consistent lookup
- **SpeechHelper** - handles speech-based input
- **ListaRepository** - manages saved vocabulary
- **DeepDiveHelper** - supports the optional external linguistic deep-dive feature


## Screenshots
TO-DO: screenshots and demo video of the app will be added here. 

## Building the Project
### Requirements
- **Android Studio**
- **JDK 21**
- **Android SDK**

Clone the repository: git clone https://github.com/ElGavaOriginal/lleva-tilde.git

Open the project in Android Studio and allow Gradle to synchronize the required dependencies.

For command line builds: ./gradlew :llevatilde:assembleDebug
 * A local local.properties file containing the Android SDK location may be required> This file is intentionally excluded from version control because it contains machine-specific configuration.

## Academic Context

¿Lleva Tilde? was originally developed for Northeastern University's Khoury College of Computer Science **CS5520: Mobile Application Development's** coursework. Subsequently it was prepared as a standalone portfolio project. 

The application reflects my broader interest in the intersection of **computer science, linguistics, and second-language learning technology**. 

## Linguistic Reference

The application's discussion of Spanish accentuation rules is based on conventions published by the **Real Academia Española (RAE)**.

_______________________________________________________________________________________________

## Developed by Ruben Salido

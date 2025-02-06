import java.io.File

object CaesarCipher {
    private const val ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

    fun encrypt(text: String, shift: Int): String {
        return text.map { char ->
            val index = ALPHABET.indexOf(char)
            if (index != -1) {
                ALPHABET[(index + shift) % ALPHABET.length]
            } else {
                char
            }
        }.joinToString("")
    }

    fun decrypt(text: String, shift: Int): String {
        return encrypt(text, ALPHABET.length - shift % ALPHABET.length)
    }

    fun bruteForceDecrypt(text: String) {
        for (i in 1 until ALPHABET.length) {
            println("Shift $i: ${decrypt(text, i)}")
        }
    }
}

fun processFile(inputPath: String, outputPath: String, shift: Int, mode: String) {
    val inputFile = File(inputPath)
    val outputFile = File(outputPath)

    if (!inputFile.exists()) {
        println("Ошибка: файл не найден!")
        return
    }

    val text = inputFile.readText()
    val result = when (mode) {
        "encrypt" -> CaesarCipher.encrypt(text, shift)
        "decrypt" -> CaesarCipher.decrypt(text, shift)
        "brute" -> {
            CaesarCipher.bruteForceDecrypt(text)
            return
        }
        else -> {
            println("Неверный режим работы!")
            return
        }
    }

    outputFile.writeText(result)
    println("Файл успешно обработан: $outputPath")
}

fun main() {
    println("Выберите режим работы (encrypt, decrypt, brute):")
    val mode = readLine()?.trim() ?: return

    println("Введите путь к файлу с исходным текстом:")
    val inputPath = readLine()?.trim() ?: return

    println("Введите путь к файлу для сохранения результата:")
    val outputPath = readLine()?.trim() ?: return

    val shift = if (mode != "brute") {
        println("Введите ключ (сдвиг):")
        readLine()?.toIntOrNull() ?: return
    } else 0

    processFile(inputPath, outputPath, shift, mode)
}

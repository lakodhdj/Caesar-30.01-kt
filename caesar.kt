import java.io.File

// Объект, содержащий методы шифрования, дешифрования и взлома
object CaesarCipher {
    const val ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

    // Метод для шифрования текста с использованием сдвига
    fun encrypt(text: String, shift: Int): String {
        return text.map { char ->
            val index = ALPHABET.indexOf(char)
            if (index != -1) {
                // Сдвигаем символ с учетом длины алфавита
                ALPHABET[(index + shift) % ALPHABET.length]
            } else {
                char // Если символ не из алфавита, оставляем его без изменений
            }
        }.joinToString("")
    }

    // Метод для расшифровки с известным ключом
    fun decrypt(text: String, shift: Int): String {
        return encrypt(text, ALPHABET.length - shift % ALPHABET.length) // Обратный сдвиг
    }

    // Метод brute force (перебор всех возможных ключей)
    fun bruteForceDecrypt(text: String) {
        for (shift in 1 until ALPHABET.length) {
            println("Ключ $shift: ${decrypt(text, shift)}")
        }
    }

    // Статистический анализ (ищет самую частую букву в тексте и предполагает, что это 'E')
    fun frequencyAnalysisDecrypt(text: String): String {
        val commonLetter = 'E' // Самая частая буква в английском языке
        val mostFrequentLetter = text.filter { it.isLetter() }
            .groupingBy { it.uppercaseChar() }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key ?: return text // Если не удалось найти букву, возвращаем оригинальный текст

        val shift = (ALPHABET.indexOf(mostFrequentLetter) - ALPHABET.indexOf(commonLetter) + ALPHABET.length) % ALPHABET.length
        return decrypt(text, shift)
    }
}

// Метод для работы с файлами
fun processFile(inputPath: String, outputPath: String, shift: Int?, mode: String) {
    val inputFile = File(inputPath)
    val outputFile = File(outputPath)

    if (!inputFile.exists()) {
        println("Ошибка: Файл не найден!")
        return
    }

    val text = inputFile.readText()
    val result = when (mode) {
        "encrypt" -> shift?.let { CaesarCipher.encrypt(text, it) } ?: return
        "decrypt" -> shift?.let { CaesarCipher.decrypt(text, it) } ?: return
        "brute" -> {
            CaesarCipher.bruteForceDecrypt(text)
            return
        }
        "analyze" -> CaesarCipher.frequencyAnalysisDecrypt(text)
        else -> {
            println("Ошибка: Неверный режим работы!")
            return
        }
    }

    outputFile.writeText(result)
    println("Файл успешно обработан: $outputPath")
}

// Главная функция для взаимодействия с пользователем
fun main() {
    println("Выберите режим работы:")
    println("  encrypt - зашифровать текст")
    println("  decrypt - расшифровать текст (по ключу)")
    println("  brute - расшифровать методом brute force")
    println("  analyze - расшифровать методом статистического анализа")

    val mode = readLine()?.trim()?.lowercase() ?: return

    println("Введите путь к файлу с исходным текстом:")
    val inputPath = readLine()?.trim() ?: return

    println("Введите путь к файлу для сохранения результата:")
    val outputPath = readLine()?.trim() ?: return

    // Если режим требует ключ, запрашиваем его
    val shift = if (mode in listOf("encrypt", "decrypt")) {
        println("Введите ключ (сдвиг от 1 до ${CaesarCipher.ALPHABET.length - 1}):")
        readLine()?.toIntOrNull()?.takeIf { it in 1 until CaesarCipher.ALPHABET.length } ?: run {
            println("Ошибка: Некорректный ключ!")
            return
        }
    } else null

    processFile(inputPath, outputPath, shift, mode)
}

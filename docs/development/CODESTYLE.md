# Code Style Guide - Projeto Ticktzy

Este projeto utiliza ferramentas de linting e formatação para manter a consistência do código, similar ao ESLint + Prettier em projetos JavaScript.

---

## 🛠️ Ferramentas Utilizadas

### **ktlint** (Linter + Formatter)
- **Função**: ESLint para Kotlin
- **O que faz**: Formata código, detecta problemas de estilo
- **Como funciona**: Regras oficiais da Kotlin Style Guide

### **Detekt** (Static Analysis)
- **Função**: ESLint + SonarQube para Kotlin
- **O que faz**: Detecta code smells, complexidade, bugs potenciais
- **Como funciona**: Análise estática do código

### **EditorConfig**
- **Função**: Configurações compartilhadas entre editores
- **O que faz**: Mantém configurações consistentes (indent, line endings, etc)

---

## 🚀 Comandos Disponíveis

### Verificar Estilo de Código

```bash
# Verificar sem fazer alterações (como "npm run lint")
./gradlew ktlintCheck

# OU usar o script
./scripts/lint.sh
```

Este comando verifica se o código está de acordo com as regras de estilo.

### Formatar Código Automaticamente

```bash
# Formatar código (como "npm run format")
./gradlew ktlintFormat

# OU usar o script
./scripts/format.sh
```

Este comando formata o código automaticamente de acordo com as regras de estilo.

### Verificar Análise Estática

```bash
# Executar detekt (análise estática)
./gradlew detekt
```

### Executar Todas as Verificações

```bash
# Executar ktlint + detekt (como "npm run check")
./scripts/check.sh
```

### Auto-fix (Correção Automática)

```bash
# Auto-fix de problemas de estilo
./scripts/fix.sh
```

---

## 📋 Configuração no Android Studio

### 1. Configurar ktlint como Code Style

1. Abra **File → Settings → Plugins**
2. Procure por **ktlint** e instale (se não tiver)
3. Ou use a versão integrada do IntelliJ

### 2. Configurar Auto-format

1. **Settings → Tools → Actions on Save**
2. Marque: **Reformat code**
3. Marque: **Optimize imports**

### 3. Configurar EditorConfig

O arquivo `.editorconfig` já está configurado e será automaticamente aplicado no Android Studio.

---

## 📝 Regras de Estilo

### Indentação
- **4 espaços** para Kotlin (`*.kt`, `*.kts`)
- **2 espaços** para XML e JSON

### Comprimento de Linha
- **Máximo**: 120 caracteres
- **Soft wrap**: 120 caracteres

### Naming Conventions

```kotlin
// Classes: PascalCase
class PaymentBar { }

// Funções: camelCase
fun processPayment() { }

// Constantes: UPPER_SNAKE_CASE
const val API_URL = "https://..."
val constant = "value"

// Variáveis: camelCase
var userName = "John"

// Props privadas: _leadingUnderscore
private var _state = MutableStateFlow()
```

### Importações

```kotlin
// Ordem:
// 1. Kotlin stdlib
import kotlinx.coroutines.flow.*

// 2. Android
import android.os.Bundle
import androidx.lifecycle.*

// 3. Third-party
import retrofit2.*
import com.squareup.retrofit2.*

// 4. Local
import br.com.sttsoft.ticktzy.core.*
import br.com.sttsoft.ticktzy.domain.*
```

### Classes e Objetos

```kotlin
// Classes: iniciar com comentário breve se necessário
class PaymentProcessor {
    // ...
}

// Objetos: para singletons
object NetworkManager {
    // ...
}

// Data classes: use quando apropriado
data class User(
    val id: String,
    val name: String
)
```

### Funções

```kotlin
// Funções curtas (< 50 linhas)
fun calculateTotal(): Double {
    // ...
}

// Corpo de expressão quando possível
fun isAvailable() = items.isNotEmpty()

// Parâmetros: um por linha se >= 3
fun processPayment(
    amount: Double,
    paymentType: PaymentType,
    onSuccess: (String) -> Unit
) {
    // ...
}
```

### Strings

```kotlin
// Preferir template strings
val message = "Total: $total"

// Múltiplas linhas
val query = """
    SELECT * FROM users
    WHERE id = $userId
""".trimIndent()
```

### Null Safety

```kotlin
// Sempre preferir safe calls
user?.name

// Elvis operator para defaults
val name = user?.name ?: "Unknown"

// Let para scope
user?.let {
    processUser(it)
}
```

### When Expressions

```kotlin
// Preferir when em vez de if-else
when (status) {
    is Success -> handleSuccess()
    is Error -> handleError()
    else -> {}
}

// Com retorno
val result = when (type) {
    "credit" -> 1
    "debit" -> 2
    else -> 0
}
```

---

## 🔍 Regras do Detekt

### Regras Ativas

- **LongMethod**: Funções com mais de 75 linhas
- **LongParameterList**: Mais de 6 parâmetros
- **ComplexMethod**: Complexidade ciclomática > 15
- **MaxLineLength**: Linhas com mais de 120 caracteres
- **MagicNumber**: Números mágicos (ignora 0, 1, 2, -1)
- **ForbiddenComment**: Bloqueia TODO:, FIXME:, STOPSHIP:

### Regras Desabilitadas

- **UndocumentedPublicClass**: Documentação pública não obrigatória
- **UndocumentedPublicFunction**: Documentação pública não obrigatória

---

## 📊 Relatórios

Os relatórios são gerados automaticamente após cada execução:

### ktlint
- **Arquivo**: `build/reports/ktlint/main.html`
- **Formato**: HTML
- **Localização**: Auto ao executar `ktlintCheck`

### Detekt
- **Arquivo**: `build/reports/detekt.html`
- **Formato**: HTML
- **Localização**: Auto ao executar `detekt`

---

## 🎯 Integração com Git

### Pre-commit Hook (Opcional)

Criar arquivo `.git/hooks/pre-commit`:

```bash
#!/bin/bash

echo "Running ktlint..."
./gradlew ktlintCheck

if [ $? -ne 0 ]; then
    echo "❌ ktlint check failed!"
    exit 1
fi

echo "✅ Pre-commit check passed!"
exit 0
```

Tornar executável:
```bash
chmod +x .git/hooks/pre-commit
```

### CI/CD Integration

Adicionar ao pipeline:

```yaml
# .github/workflows/ci.yml
- name: Check code style
  run: ./gradlew ktlintCheck

- name: Check code quality
  run: ./gradlew detekt
```

---

## 🆘 Troubleshooting

### Erro: "ktlintCheck failed"

```bash
# Auto-fix
./gradlew ktlintFormat

# Ver detalhes
./gradlew ktlintCheck --info
```

### Erro: "detekt failed"

```bash
# Ver relatório detalhado
cat build/reports/detekt.html

# Criar baseline para ignorar problemas existentes
./gradlew detektBaseline
```

### Ignorar arquivos

Editar `ktlint.gradle.kts`:
```kotlin
filter {
    exclude("**/generated/**")
    exclude("**/R.kt")
    exclude("**/BuildConfig.kt")
}
```

---

## 📚 Equivalências JS → Kotlin

| JavaScript | Kotlin |
|------------|--------|
| `npm run lint` | `./gradlew ktlintCheck` |
| `npm run format` | `./gradlew ktlintFormat` |
| `npm run check` | `./gradlew ktlintCheck detekt` |
| `npm run fix` | `./gradlew ktlintFormat` |
| ESLint | ktlint |
| Prettier | ktlint (formatter) |
| SonarQube | Detekt |

---

## 📝 Exemplo de Uso

### Antes (sem formatação)
```kotlin
class   PaymentBar {
fun processPayment(amount:Double,type:String):String{
val fee=amount*0.05
return "$${amount+fee}"
}
}
```

### Depois (formatado automaticamente)
```kotlin
class PaymentBar {
    fun processPayment(amount: Double, type: String): String {
        val fee = amount * 0.05
        return "$${amount + fee}"
    }
}
```

---

## ✅ Checklist

- [ ] Instalado ktlint e detekt
- [ ] Executar `./gradlew ktlintFormat` para formatar código existente
- [ ] Executar `./scripts/check.sh` para verificar tudo
- [ ] Configurar auto-format no Android Studio
- [ ] Adicionar pre-commit hook (opcional)
- [ ] Configurar CI/CD (opcional)

---

**Última atualização**: Janeiro 2025


# Caja Clara

> Tu tienda, clara como el agua.

**Caja Clara** es una app Android **local y sin servidor** para gestionar una tienda
física pequeña o mediana: productos, coste/PVP, margen, stock, agotados, ventas diarias
y estadísticas. Toda la interfaz está en español y la moneda es el **peso cubano (CUP)**.

[![CI](https://github.com/aggiovato/caja-clara/actions/workflows/ci.yml/badge.svg)](https://github.com/aggiovato/caja-clara/actions/workflows/ci.yml)
![Versión](https://img.shields.io/badge/versión-0.1.0-blue)
![Plataforma](https://img.shields.io/badge/Android-minSdk%2024-green)

---

## ¿Qué resuelve?

Que el tendero sepa, sin complicaciones, **cuánto gana de verdad**: qué le cuesta cada
producto, a cómo lo vende, cuánto margen le deja y cuánto ha ganado al cerrar el día.
Funciona **sin internet** y guarda todo en el propio dispositivo.

## Características (MVP)

- Crear y editar productos (coste y PVP) con **historial de precios**.
- Cálculo de **margen** unitario, porcentual y markup.
- Marcar **agotado** / reactivar / pausar (sin borrado físico).
- Registrar **ventas** con *snapshot* de coste/PVP (cambiar precios no altera balances pasados).
- **Ganancia manual diaria**, separada de la calculada por ventas.
- **Balance del día** y estadísticas por rango (hoy/semana/mes).

## Stack técnico

- **Kotlin** + **Jetpack Compose** (Material 3) · tipografía **Barlow** (empaquetada, offline)
- **ViewModel + StateFlow** (UDF)
- **Room** (persistencia local) · **Coroutines + Flow**
- **Navigation Compose** · **Hilt** (inyección de dependencias)
- Tests: JUnit, Turbine, MockK, Compose UI Test, Room Testing

## Arquitectura

Hexagonal adaptada — el dominio no conoce Android, Room ni Compose:

```
UI (Compose) → ViewModel → UseCase → RepositoryPort → RoomRepository → DAO → SQLite
```

Organización **feature-first** (Screaming Architecture): el nivel superior grita el
negocio, no la tecnología. Cada feature lleva sus capas `domain`/`data`; la UI se mantiene
como núcleo aparte y los primitivos compartidos en `core` (raíz `com.cajaclara.app`):

```
core/{money,quantity,date,result,dispatcher}     primitivos compartidos (Money, Quantity…)
feature/products/{domain,data}                   Product, Category, Margin, ProductStatus…
feature/sales/{domain,data}                       Sale, SaleLine, DailyProfit…
feature/stock/{domain,data}                       StockMovement…
feature/stats/{domain,data}                       DailyBalance, balances por rango
ui/{designsystem,navigation,home,…}              núcleo de UI (Compose), separado
di/{DatabaseModule,RepositoryModule,UseCaseModule}
```

Decisiones no negociables: dinero en **céntimos como `Long`** (nunca `Float/Double`),
ventas con *snapshot*, productos que no se borran físicamente.

## Requisitos

- **JDK 17**
- **Android SDK** con `compileSdk 37` (Gradle lo descarga solo) · `minSdk 24` · `targetSdk 36`
- [Task](https://taskfile.dev) (opcional, para los atajos)

## Puesta en marcha

```bash
# con Task
task build       # compila el APK de debug
task test        # tests unitarios
task preflight   # lint + tests + build (pásalo antes de hacer commit)

# o directamente con Gradle
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
```

Ejecuta `task` sin argumentos para ver todas las tareas.

## Versionado y releases

La versión vive en `gradle.properties` (`VERSION_NAME`, `VERSION_CODE`) como fuente
única de verdad. Se usa **SemVer**.

```bash
task version:show           # versión actual
task version:minor          # 0.1.0 → 0.2.0 (y +1 al versionCode)
git commit -am "release: v0.2.0"
task version:tag            # crea el tag anotado v0.2.0
git push --follow-tags      # dispara el workflow de Release
```

Al subir un tag `v*`, el workflow **Release** compila el APK y publica una *GitHub Release*
con el binario adjunto. (El APK sale **sin firmar** hasta que se configure un keystore con
secrets del repo.)

## Estado del proyecto

| Fase | Descripción | Estado |
| --- | --- | --- |
| 0 | Preparación (Gradle, Hilt, Room, Navigation, tema) | ✅ |
| 1 | Design System y navegación | ⬜ |
| 2 | Dominio base (`Money`, `Margin`, modelos, value objects) | 🔄 `Money` y `Margin` listos |
| 3 | Base de datos Room | ⬜ |
| 4 | Gestión de productos | ⬜ |
| 5 | Coste, PVP e historial | ⬜ |
| 6 | Stock | ⬜ |
| 7 | Ventas y ganancias diarias | ⬜ |
| 8 | Estadísticas | ⬜ |
| 9 | Calidad, backup y pulido | ⬜ |

## Palabras clave / topics

`android` · `kotlin` · `jetpack-compose` · `material3` · `room` · `hilt` ·
`mvvm` · `clean-architecture` · `hexagonal-architecture` · `pos` ·
`punto-de-venta` · `gestion-tienda` · `inventario` · `offline-first` · `cuba`

> Nota: a diferencia de un proyecto web, en Android **no hay un `package.json`** que
> centralice `description`/`keywords`. Esa metadata se reparte: el identificador y la
> versión de la app viven en Gradle (`applicationId`, `versionName`), la descripción
> pública en este README, y las *keywords* se configuran como **Topics** en la página
> del repositorio en GitHub (sección *About*). Conviene añadir allí los topics de arriba.

## Licencia

Pendiente de definir.

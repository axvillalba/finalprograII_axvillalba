# Vinoteca SommWine - Sistema de Inventario de Vinos

Proyecto final desarrollado en Java para la gestión de inventario de una vinoteca.

El sistema permite administrar vinos mediante una interfaz gráfica (Swing), aplicando conceptos de Programación Orientada a Objetos (POO), herencia, interfaces, CRUD genérico y persistencia de datos.

---

## Autor

- **Axel Villalba**

---

## Tecnologías utilizadas

- **Java**
- **Swing (GUI)**
- **NetBeans**
- **Programación Orientada a Objetos**
- **Colecciones genéricas (`List`)**
- **Persistencia en archivos (`.csv`, `.txt`, `.dat`)**

---

## Funcionalidades principales

### Gestión de inventario
- Alta de vinos
- Modificación de vinos
- Eliminación de vinos
- Visualización de stock en tabla

### Tipos de vino (Herencia)
- `Tinto`
- `Blanco`
- `Rosado`

Todos heredan de la clase abstracta `Vino`.

### Búsqueda avanzada
Permite filtrar vinos por:
- Tipo de vino
- Uva
- Bodega
- Nombre

### Importación de stock por CSV
- Carga masiva desde archivo `.csv`
- Validación de datos
- Si el vino ya existe, se suma el stock automáticamente (sin duplicar ítems)

### Exportación de datos
- **CSV** (compatible con la importación)
- **TXT** (informe legible con encabezado y detalle por vino)

### Persistencia binaria (serialización)
- Implementación en `.dat` para guardar/cargar inventario completo  
*(la lógica está implementada en `AlmacenamientoDat`, y puede conectarse a la GUI si se desea)*

---

## Estructura del proyecto

### `modelo`
Contiene las clases del dominio:
- `Vino` (abstracta)
- `Tinto`
- `Blanco`
- `Rosado`
- `Maridaje` (interfaz)
- Enums (`Aroma`, `Barrica`, `TipoCrianza`, `UvaTinta`, etc.)

### `servicio`
Lógica de negocio:
- `ICrud<T>` (interfaz genérica)
- `GestorVinos` (CRUD + búsquedas + suma de stock)

### `persistencia`
Manejo de archivos:
- `CsvVinosImportador`
- `ExportadorVinos`
- `AlmacenamientoDat`

### `gui`
Interfaz gráfica en Swing:
- `Principal`
- `BusquedaAvanzada`
- `Modificar`
- `Editar`
- `CargarVino`

---

## Diseño orientado a objetos aplicado

El proyecto implementa:
- **Herencia**: `Tinto`, `Blanco` y `Rosado` extienden `Vino`
- **Polimorfismo**: uso de `List<Vino>` para manejar distintos subtipos
- **Abstracción**: `Vino` como clase abstracta
- **Interfaces**:
  - `Maridaje` para comportamiento específico (`sugerirMaridaje`)
  - `ICrud<T>` para operaciones genéricas del gestor
- **Encapsulamiento**: atributos privados/protegidos con getters y setters

---

## Formato CSV utilizado (importación/exportación)

El sistema usa el siguiente formato de columnas:

tipo;nombre;bodega;anio;tipoCrianza;aroma;barrica;precio;stock;uva;taninos;cuerpo;dulzor;acidez;tempServicio;metodoRosado

### Ejemplo (Tinto)
TINTO;Rutini Malbec;Rutini;2021;RESERVA;FRUTAL;ROBLE_FRANCES;12500;12;MALBEC;MEDIO;MEDIO;;;;

### Ejemplo (Blanco)
BLANCO;Norton Chardonnay;Norton;2022;JOVEN;CITRICO;SIN_BARRICA;7200;15;CHARDONNAY;;;SECO;MEDIA;10;

### Ejemplo (Rosado)
ROSADO;Rose de Verano;Nieto Senetiner;2023;JOVEN;FRUTAL;SIN_BARRICA;7600;18;PINOT_NOIR;;;SECO;;;PRENSADO_DIRECTO

---

## Cómo ejecutar el proyecto

1. Abrir el proyecto en **NetBeans**
2. Ejecutar la clase principal:
   - `finalprograii_villalbaaxel.FinalPrograII_VillalbaAxel`
3. Se cargan vinos de prueba automáticamente para testear la interfaz

---

## Archivos generados
El sistema puede generar:
- `.csv` (exportación de inventario)
- `.txt` (informe legible de stock)
- `.dat` (serialización del inventario, implementación disponible)

---

## Capturas de pantalla

- Pantalla principal 
![Pantalla de Inicio](https://github.com/axvillalba/finalprograII_axvillalba/blob/main/FinalPrograII_VillalbaAxel/img/1.png)

- Búsqueda avanzada
![Busqueda avanzada](https://github.com/axvillalba/finalprograII_axvillalba/blob/main/FinalPrograII_VillalbaAxel/img/2.png)


- Modificación / edición
![Modificación](https://github.com/axvillalba/finalprograII_axvillalba/blob/main/FinalPrograII_VillalbaAxel/img/3.png)
![Edicion](https://github.com/axvillalba/finalprograII_axvillalba/blob/main/FinalPrograII_VillalbaAxel/img/4.png)

- Importación CSV 
![Cargar vinos](https://github.com/axvillalba/finalprograII_axvillalba/blob/main/FinalPrograII_VillalbaAxel/img/5.png)

- Exportación TXT/CSV 
![Exportar archivos](https://github.com/axvillalba/finalprograII_axvillalba/blob/main/FinalPrograII_VillalbaAxel/img/6.png)

---

## Observaciones
Este proyecto fue desarrollado con enfoque académico, priorizando:
- claridad del modelo orientado a objetos
- separación por capas (modelo / servicio / persistencia / GUI)
- validación de datos
- facilidad de mantenimiento y extensión

---

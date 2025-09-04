# TimerPlugin para Minecraft

Un plugin avanzado para crear temporizadores visuales en Minecraft con displays de bloques personalizables.

## Características

- 🕐 Temporizadores visuales con display de bloques
- 🎨 Personalización de colores para números y separadores
- ⚡ Funciona incluso cuando el servidor está vacío
- 🎯 Fácil configuración mediante comandos y archivo YAML
- 🔧 Actualización en tiempo real de los materiales
- 💥 Efectos visuales y sonoros al activarse

## Instalación

1. Descarga el archivo `TimerPlugin.jar` desde los releases
2. Colócalo en la carpeta `plugins` de tu servidor
3. Reinicia o inicia el servidor
4. El plugin creará automáticamente el archivo de configuración

## Comandos

### Para administradores:
- `/gettimer` - Obtiene un item de temporizador avanzado
- `/settimer <tiempo> <unidad>` - Configura el tiempo del temporizador
  - Unidades: `s` (segundos), `m` (minutos), `h` (horas), `d` (días)
  - Ejemplo: `/settimer 10 m` - Configura un temporizador de 10 minutos
- `/timerreload` - Recarga la configuración del plugin (permiso: `timerplugin.reload`)
- `/timersetmaterial <tipo> <material>` - Cambia el material del display (permiso: `timerplugin.setmaterial`)
  - Tipos: `número` (o `numero`), `separador`
  - Ejemplo: `/timersetmaterial número GOLD_BLOCK`

## Permisos

- `timerplugin.reload` - Permite recargar la configuración
- `timerplugin.setmaterial` - Permite cambiar los materiales del temporizador

## Configuración

El plugin crea un archivo `config.yml` en la carpeta del plugin con las siguientes opciones:

```yaml
# Material para los números del temporizador
number-material: IRON_BLOCK

# Material para los separadores (puntos) del temporizador
separator-material: REDSTONE_BLOCK
```

### Materiales populares:

**Para números:**
- `GOLD_BLOCK` - Bloque de oro
- `DIAMOND_BLOCK` - Bloque de diamante
- `EMERALD_BLOCK` - Bloque de esmeralda
- `IRON_BLOCK` - Bloque de hierro
- `LAPIS_BLOCK` - Bloque de lapislázuli

**Para separadores:**
- `REDSTONE_BLOCK` - Bloque de redstone
- `GLOWSTONE` - Piedra luminosa
- `SEA_LANTERN` - Linterna de mar
- `REDSTONE_LAMP` - Lámpara de redstone

## Uso

1. Usa `/gettimer` para obtener un temporizador
2. Configura el tiempo con `/settimer` (ej: `/settimer 30 s` para 30 segundos)
3. Haz clic derecho en un bloque con el temporizador en la mano para colocarlo
4. El temporizador comenzará una cuenta regresiva y se activará cuando llegue a cero

## Características técnicas

- Los temporizadores funcionan incluso cuando no hay jugadores en el servidor
- Los chunks se cargan automáticamente cuando es necesario
- Los displays se actualizan en tiempo real al cambiar la configuración
- Sistema eficiente que no afecta el rendimiento del servidor

## Soporte

Si encuentras algún error o tienes sugerencias, por favor abre un issue en el repositorio del plugin.

## Licencia

Este plugin está bajo la licencia MIT.

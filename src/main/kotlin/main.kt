import DataTransferObjects.DTO
import dbAccess.HikariDataSource
import org.slf4j.LoggerFactory
import kotlin.IndexOutOfBoundsException

data class Ctf(val id: Int, val grupoId: Int, val puntuacion: Int): DTO()
data class Grupo(val grupoid: Int, val mejorCtfId: Int = 0)


/*
TODO
 | 1 | `-a <ctfid> <grupoId> <puntuacion>`
 | Añade una participación del grupo `<grupoid>` en el CTF `<ctfid>` con la puntuación `<puntuacion>`.
 Recalcula el campo `mejorposCTFid` de los grupos en la tabla `GRUPOS`. |

| 2 | `-d <ctfid> <grupoId>`
| Elimina la participación del grupo `<grupoid>` en el CTF `<ctfid>`.
Recalcula el campo `mejorposCTFid` de los grupos en la tabla `GRUPOS`. |

| 3 | `-l <grupoId>`
| Si `<grupoId>` esta presente muestra la información del grupo `<grupoId>`,
sino muestra la información de todos los grupos.    |
*/

class InvalidArgument : Exception(){
    override val message: String?
        get() = "ERROR: El número de parametros no es adecuado."
}

fun main(args: Array<String>) {

    val hikariDS = HikariDataSource()
    val ctfDao = CTFDAO(hikariDS.dataSource)

    val logger = LoggerFactory.getLogger("CTFDAO")
    logger.isDebugEnabled

    try {
        when(args[0]) {
            "-a" -> {
                logger.debug("Option a")
                ctfDao.createCTF(Ctf(args[1].toInt(), args[2].toInt(), args[3].toInt()))
            }
            "-d" -> {
                logger.debug("Option d")
                ctfDao.deleteById(args[1].toInt(), args[2].toInt())
            }
            "-l" -> {
                logger.debug("Option l")
                if (ctfDao.getById(args[1].toInt()) == null) {
                    ctfDao.getAll().forEach {
                        println()
                    }
                } else { println(ctfDao.getById(args[1].toInt())?.let {
                    """Procesado: Listado participación del grupo ${it.grupoId}
                                GRUPO: ${it.id}   ${it.grupoId}  MEJORCTF: ${it.puntuacion} it"""
                })
                }
            }
            else -> throw InvalidArgument()
            }

    } catch(e: IndexOutOfBoundsException) {
        logger.debug("Option None")
        throw e
    }

    val participaciones = listOf(
        Ctf(1, 1, 3),
        Ctf(1, 2, 101),
        Ctf(2, 2, 3),
        Ctf(2, 1, 50),
        Ctf(2, 3, 1),
        Ctf(3, 1, 50),
        Ctf(3, 3, 5)
    )
    val mejoresCtfByGroupId = calculaMejoresResultados(participaciones)
    println(mejoresCtfByGroupId)

}

/**
 * TODO
 *
 * @param participaciones
 * @return devuelve un mutableMapOf<Int, Pair<Int, Ctf>> donde
 *      Key: el grupoId del grupo
 *      Pair:
 *          first: Mejor posición
 *          second: Objeto CTF el que mejor ha quedado
 */
private fun calculaMejoresResultados(participaciones: List<Ctf>): MutableMap<Int, Pair<Int, Ctf>> {
    val participacionesByCTFId = participaciones.groupBy { it.id }
    var participacionesByGrupoId = participaciones.groupBy { it.grupoId }
    val mejoresCtfByGroupId = mutableMapOf<Int, Pair<Int, Ctf>>()
    participacionesByCTFId.values.forEach { ctfs ->
        val ctfsOrderByPuntuacion = ctfs.sortedBy { it.puntuacion }.reversed()
        participacionesByGrupoId.keys.forEach { grupoId ->
            val posicionNueva = ctfsOrderByPuntuacion.indexOfFirst { it.grupoId == grupoId }
            if (posicionNueva >= 0) {
                val posicionMejor = mejoresCtfByGroupId.getOrDefault(grupoId, null)
                if (posicionMejor != null) {
                    if (posicionNueva < posicionMejor.first)
                        mejoresCtfByGroupId.set(grupoId, Pair(posicionNueva, ctfsOrderByPuntuacion.get(posicionNueva)))
                } else
                    mejoresCtfByGroupId.set(grupoId, Pair(posicionNueva, ctfsOrderByPuntuacion.get(posicionNueva)))

            }
        }
    }
    return mejoresCtfByGroupId
}
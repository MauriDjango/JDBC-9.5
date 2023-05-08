import DataTransferObjects.DTO
import org.slf4j.LoggerFactory
import java.sql.SQLException
import javax.sql.DataSource

interface DAO {
    fun getById(id: Int): DTO?
    fun getAll(): List<DTO>
}

class CTFDAO(private val dataSource: DataSource): DAO {

    val logger = LoggerFactory.getLogger("CTFDAO")

    override fun getAll(): List<Ctf> {
        val sql = "select * from CTFS"

        try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    var resultFound = false
                    val result = stmt.executeQuery()
                    var listCtf = mutableListOf<Ctf>()

                    if (result.next()) {
                        resultFound = true
                        listCtf.add(
                            Ctf(
                            id = result.getInt("CTFid"),
                            grupoId = result.getInt("grupoid"),
                            puntuacion = result.getInt("puntuacion")
                            )
                        )
                    }
                    return listCtf
                }
            }
        } catch (dbError: SQLException) {
            logger.error(dbError.errorCode.toString())
            logger.error(dbError.sqlState)
            throw dbError

        }
    }

    // TODO question - if an inherited method changes from the superclass. Does the superclass need to reflect the change or is it fine leaving as is.
    override fun getById(id: Int): Ctf? {
        val sql = "select * from CTFS where CTFid = ?"

        try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, id)
                    var resultFound = false
                    val result = stmt.executeQuery()
                    var ctf: Ctf? = null

                    if (result.next()) {
                        resultFound = true
                        ctf = Ctf(
                            id = result.getInt("CTFid"),
                            grupoId = result.getInt("grupoid"),
                            puntuacion = result.getInt("puntuacion")
                        )
                    }
                    return ctf
                }
            }
        } catch (dbError: SQLException) {
            logger.error(dbError.errorCode.toString())
            logger.error(dbError.sqlState)
            throw dbError
        }
    }

    //TODO
    fun deleteById(id: Int, groupId: Int) {
        val sql = "delete * from CTFS where CTFid = ? and grupoid = ?"
        try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, id)
                    stmt.setInt(2, groupId)

                    val result = stmt.executeUpdate()
                    if (result == 0) {
                        logger.warn("No line has been deleted")
                        throw (SQLException())
                    } else {
                        println("Procesado: Eliminada participación del grupo $groupId en el CTF $id.")
                    }
                }
            }
        } catch (dbError: SQLException) {
            logger.error(dbError.errorCode.toString())
            logger.error(dbError.sqlState)
            throw dbError
        }
    }

    //TODO
    fun createCTF(ctf:Ctf) {
        val sql = "insert into  CTFS values(?, ?, ?)"
        try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, ctf.id)
                    stmt.setInt(2, ctf.grupoId)
                    stmt.setInt(3, ctf.puntuacion)

                    val result = stmt.executeUpdate()
                    if (result == 0) {
                        logger.warn("No line has been inserted")
                        throw (SQLException())
                    } else if (result > 0) {
                        println("Procesado: Añadida participación del grupo ${ctf.grupoId} en el CTF 1 con una puntuación de ${ctf.puntuacion} puntos.")
                    }
                }
            }
        } catch (dbError: SQLException) {
            logger.error(dbError.errorCode.toString())
            throw dbError
        }
    }
}
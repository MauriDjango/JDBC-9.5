package dbAccess

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.io.PrintWriter
import java.sql.Connection
import java.util.logging.Logger
import javax.sql.DataSource

class d

class HikariDataSource() {

    lateinit var dataSource: DataSource

    init {
        val config = HikariConfig()
        config.jdbcUrl = "jdbc:h2:./default"
        config.username = "user"
        config.password = "user"
        config.driverClassName = "org.h2.Driver"
        config.maximumPoolSize = 10
        config.isAutoCommit = true
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        dataSource = HikariDataSource(config)
    }
}
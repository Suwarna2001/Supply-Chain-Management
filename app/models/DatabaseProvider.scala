package models

import slick.jdbc.JdbcBackend.Database

trait DatabaseProvider {
  def database: Database
}
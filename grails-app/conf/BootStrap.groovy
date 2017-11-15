import com.pathunt.User
import com.pathunt.Role
import com.pathunt.UserRole

class BootStrap {

    def init = { servletContext ->
        def userCount = User.findAll().size()
        if(!userCount) {
            User admin = new User(username: 'admin', password: 'admin', enabled: true).save(failOnError: true)
            User user1 = new User(username: 'user1', password: 'user1', enabled: true).save(failOnError: true)
            User user2 = new User(username: 'user2', password: 'user2', enabled: true).save(failOnError: true)
            User user3 = new User(username: 'user3', password: 'user3', enabled: true).save(failOnError: true)
            User user4 = new User(username: 'user4', password: 'user4', enabled: true).save(failOnError: true)
            User user5 = new User(username: 'user5', password: 'user5', enabled: true).save(failOnError: true)
            def roleCount = Role.findAll().size()
            if (!roleCount) {
                Role role_admin = new Role(authority: 'ROLE_ADMIN').save()
                def userRole = UserRole.findAll().size()
                if (!userRole) {
                    UserRole.create(admin, role_admin)
                    UserRole.create(user1, role_admin)
                    UserRole.create(user2, role_admin)
                    UserRole.create(user3, role_admin)
                    UserRole.create(user4, role_admin)
                    UserRole.create(user5, role_admin)
                }
            }
        }
    }
    def destroy = {
    }
}

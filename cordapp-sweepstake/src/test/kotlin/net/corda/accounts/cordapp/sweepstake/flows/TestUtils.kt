package net.corda.accounts.cordapp.sweepstake.flows

import net.corda.accounts.service.KeyManagementBackedAccountService
import net.corda.core.utilities.getOrThrow

class TestUtils {

companion object {
    val JAPAN: String = "Japan"
    val BELGIUM: String = "Belgium"
    val REQUIRED_CORDAPP_PACKAGES = listOf(
            "net.corda.accounts.cordapp.sweepstake.states",
            "net.corda.accounts.cordapp.sweepstake.contracts",
            "net.corda.accounts.cordapp.sweepstake.flows",
            "net.corda.accounts.cordapp.sweepstake.service",
            "net.corda.accounts.service",
            "net.corda.accounts.contracts",
            "net.corda.accounts.flows")
    }

}
internal fun createAccountsForNode(accountService: KeyManagementBackedAccountService) {
    accountService.createAccount("TEST_ACCOUNT_1").getOrThrow()
    accountService.createAccount("TEST_ACCOUNT_2").getOrThrow()
    accountService.createAccount("TEST_ACCOUNT_3").getOrThrow()
    accountService.createAccount("TEST_ACCOUNT_4").getOrThrow()
    accountService.createAccount("TEST_ACCOUNT_5").getOrThrow()
    accountService.createAccount("TEST_ACCOUNT_6").getOrThrow()
    accountService.createAccount("TEST_ACCOUNT_7").getOrThrow()
    accountService.createAccount("TEST_ACCOUNT_8").getOrThrow()
}

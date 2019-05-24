package net.corda.accounts.cordapp.sweepstake.service

import co.paralleluniverse.fibers.Suspendable
import net.corda.accounts.cordapp.sweepstake.flows.IssueAccountToGroupFlow
import net.corda.accounts.cordapp.sweepstake.flows.UpdateAccountGroupFlow
import net.corda.accounts.cordapp.sweepstake.flows.generateGroupIdsForAccounts
import net.corda.accounts.cordapp.sweepstake.flows.splitAccountsIntoGroupsOfFour
import net.corda.accounts.cordapp.sweepstake.states.AccountGroup
import net.corda.accounts.states.AccountInfo
import net.corda.core.concurrent.CordaFuture
import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.FlowLogic
import net.corda.core.identity.Party
import net.corda.core.internal.concurrent.doneFuture
import net.corda.core.node.AppServiceHub
import net.corda.core.node.services.CordaService
import net.corda.core.serialization.SingletonSerializeAsToken
import net.corda.core.utilities.getOrThrow

@CordaService
class TournamentService(val services: AppServiceHub) : SingletonSerializeAsToken() {

    //This is atrocious
    //I'm sorry
    //Bah
    @Suspendable
    fun assignAccountsToGroups(accounts: List<StateAndRef<AccountInfo>>, numOfTeams: Int, otherParty: Party) {
        val accountGroups = splitAccountsIntoGroupsOfFour(accounts)
        val groupIds = generateGroupIdsForAccounts(accounts.size, numOfTeams)
        //TODO require same size
        val map = groupIds.zip(accountGroups)

        map.forEach {
            assignAccountsToGroup(it.first, it.second, otherParty)
        }
    }

    private fun assignAccountsToGroup(groupId: Int, accounts: List<StateAndRef<AccountInfo>>, otherParty: Party) {
        val initialState = flowAwareStartFlow(IssueAccountToGroupFlow(otherParty, accounts.first(), groupId)).toCompletableFuture().getOrThrow()
        updateStates(accounts.drop(1), initialState, otherParty)
    }

    private fun updateStates(accounts: List<StateAndRef<AccountInfo>>, state: StateAndRef<AccountGroup>, otherParty: Party) {
        while (accounts.isNotEmpty()) {
            val newState = flowAwareStartFlow(UpdateAccountGroupFlow(otherParty, accounts.first(), state.state.data.linearId)).toCompletableFuture().getOrThrow()
            return if (accounts.size == 1) {
            } else {
                updateStates(accounts.drop(1), newState, otherParty)
            }
        }
    }

    @Suspendable
    inline fun <reified T : Any> flowAwareStartFlow(flowLogic: FlowLogic<T>): CordaFuture<T> {
        val currentFlow = FlowLogic.currentTopLevel
        return if (currentFlow != null) {
            val result = currentFlow.subFlow(flowLogic)
            doneFuture(result)
        } else {
            this.services.startFlow(flowLogic).returnValue
        }
    }
}
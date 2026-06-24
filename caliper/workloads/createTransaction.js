'use strict';

const { WorkloadModuleBase } = require('@hyperledger/caliper-core');

class CreateTransactionWorkload extends WorkloadModuleBase {
    async initializeWorkloadModule(workerIndex, totalWorkers, roundIndex, roundArguments, sutAdapter, sutContext) {
        await super.initializeWorkloadModule(workerIndex, totalWorkers, roundIndex, roundArguments, sutAdapter, sutContext);
        this.transactionIndex = 0;
        this.contractId = this.roundArguments.contractId;
    }

    async submitTransaction() {
        const transactionId = `${this.roundIndex}-${this.workerIndex}-${this.transactionIndex++}`;
        const timestamp = new Date().toISOString();
        const sender = `sender-${this.workerIndex}`;
        const receiver = `receiver-${this.workerIndex}`;
        const amount = '100.00';
        const transactionType = 'PAYMENT';
        const request = {
            contractId: this.contractId,
            contractFunction: 'CreateTransaction',
            contractArguments: [
                transactionId,
                timestamp,
                sender,
                receiver,
                amount,
                transactionType
            ],
            readOnly: false
        };
        return this.sutAdapter.sendRequests(request);
    }
}

function createWorkloadModule() {
    return new CreateTransactionWorkload();
}

module.exports.createWorkloadModule = createWorkloadModule; 
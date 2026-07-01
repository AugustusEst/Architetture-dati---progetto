'use strict';

const { WorkloadModuleBase } = require('@hyperledger/caliper-core');

class UpdateTransactionWorkload extends WorkloadModuleBase {
    async initializeWorkloadModule(workerIndex, totalWorkers, roundIndex, roundArguments, sutAdapter, sutContext) {
        await super.initializeWorkloadModule(workerIndex, totalWorkers, roundIndex, roundArguments, sutAdapter, sutContext);
        this.transactionIndex = 0;
        this.contractId = this.roundArguments.contractId;
        this.assetCount = this.roundArguments.assetCount;
        /*for (let i = 0; i < this.assetCount; i++) {
            const transactionId = `${this.roundIndex}-${this.workerIndex}-${i}`;
            const request = {
                contractId: this.contractId,
                contractFunction: 'CreateTransaction',
                contractArguments: [
                    transactionId,
                    new Date().toISOString(),
                    `sender-${this.workerIndex}`,
                    `receiver-${this.workerIndex}`,
                    '100.00',
                    'PAYMENT'
                ],
                readOnly: false
            };   
            await this.sutAdapter.sendRequests(request);
        } */
    }

    async submitTransaction() {
        this.transactionIndex++;
        const transactionId = `${this.workerIndex}-${this.transactionIndex % this.assetCount}`;
        const request = {
            contractId: this.contractId,
            contractFunction: 'UpdateTransaction',
            contractArguments: [
                    transactionId,
                    new Date().toISOString(),
                    `updated-sender-${this.workerIndex}`,
                    `updated-receiver-${this.workerIndex}`,
                    '150.00',
                    'PAYMENT-UPDATED'
                ],
            readOnly: false
        };
        return this.sutAdapter.sendRequests(request);
    }
}

function createWorkloadModule() {
    return new UpdateTransactionWorkload();
}

module.exports.createWorkloadModule = createWorkloadModule; 
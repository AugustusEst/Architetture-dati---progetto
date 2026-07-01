'use strict';

const { WorkloadModuleBase } = require('@hyperledger/caliper-core');

class ReadTransactionWorkload extends WorkloadModuleBase {
    async initializeWorkloadModule(workerIndex, totalWorkers, roundIndex, roundArguments, sutAdapter, sutContext) {
        await super.initializeWorkloadModule(workerIndex, totalWorkers, roundIndex, roundArguments, sutAdapter, sutContext);
        this.contractId = this.roundArguments.contractId;
        this.assetCount = this.roundArguments.assetCount;
        if (this.roundIndex === 3) {
            for (let i = 0; i < this.assetCount; i++) {
                const transactionId = `${this.workerIndex}-${i}`;
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
            }
        }   
    }

    async submitTransaction() {
        const transactionId = `${this.workerIndex}-${Math.floor(Math.random() * this.assetCount)}`;
        const request = {
            contractId: this.contractId,
            contractFunction: 'ReadTransaction',
            contractArguments: [transactionId],
            readOnly: true
        };
        return this.sutAdapter.sendRequests(request);
    }
}

function createWorkloadModule() {
    return new ReadTransactionWorkload();
}

module.exports.createWorkloadModule = createWorkloadModule; 